package vn.vnpay.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.AccountDetailBankRequest;
import vn.vnpay.commoninterface.bank.request.AccountListingBankRequest;
import vn.vnpay.commoninterface.bank.request.ExchangeRateInquiryBankRequest;
import vn.vnpay.commoninterface.bank.response.AccountDetailBankResponse;
import vn.vnpay.commoninterface.bank.response.AccountListingBankResponse;
import vn.vnpay.commoninterface.bank.response.ExchangeRateInquiryBankResponse;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.request.BaseCheckerInitRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.AuthenMethodResponse;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.commoninterface.response.InitAuthenResponse;
import vn.vnpay.commoninterface.service.*;
import vn.vnpay.config.MessagingConfig;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.entity.MbService;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.dbinterface.entity.SmeTrans;
import vn.vnpay.dbinterface.entity.SmeTransactionDetail;
import vn.vnpay.dbinterface.repository.MbServiceRepository;
import vn.vnpay.dbinterface.repository.SmeTransDetailRepository;
import vn.vnpay.dbinterface.repository.SmeTransRepository;
import vn.vnpay.request.CashPaymentRequest;
import vn.vnpay.request.InitConfirmTransBatchRequest;
import vn.vnpay.response.TransConfirmRes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CashPaymentService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private Gson gson;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CoreQueryClient coreQueryClient;

    @Autowired
    private MbServiceRepository mbServiceRepository;

    @Autowired
    private TransactionFeeService transactionFeeService;

    @Autowired
    private TransactionLimitService transactionLimitService;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    private SmeTransDetailRepository smeTransDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier(value = "firstRabbitTemplate")
    public RabbitTemplate firstRabbitTemplate;

    public BaseClientResponse makerInitCashPayment(CashPaymentRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            BaseTransactionResponse dataRp = new BaseTransactionResponse();
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case Constants.UserStatus.ACTIVE:

                        // Init transaction remark
                        StringBuilder remark = new StringBuilder();
                        if (Constants.SOURCE_IB.equals(rq.getSource())) {
                            remark.append("IBBIZ");
                        } else {
                            remark.append("MBBIZ");
                        }
                        // Kiểm tra tài khoản debit
                        List<AccountDTO> listAccount = user.getListAccount();
                        Optional<AccountDTO> opt =
                                listAccount.stream()
                                        .filter(
                                                x ->
                                                        (rq.getFromAcc().equals(x.getAccountNo())
                                                                || rq.getFromAcc().equals(x.getAccountAlias())))
                                        .findFirst();

                        if (!opt.isPresent()) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }
                        AccountDTO accPreView = opt.get();
                        // Khởi tạo thông tin metadata
                        TransactionMetaDataDTO metadata = TransactionMetaDataDTO.builder().build();
                        if (rq.getFromAcc().equals(accPreView.getAccountAlias())) {
                            rq.setAccAlias(accPreView.getAccountAlias());
                            //              rq.setFromAcc(accPreView.getAccountNo());
                        }

                        rp =
                                transactionService.checkDebitAccountStatus(
                                        rp,
                                        rq.getFromAcc(),
                                        accPreView.getAccountType(),
                                        rq.getFromAcc().length() > 10,
                                        true,
                                        rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }
                        // Lấy thông tin mã sản phẩm tài khoản debit
                        log.info("Get Debit account details for {}", rq.getFromAcc());
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(rq.getFromAcc());
                        accDetailsReq.setAccountType("D");
                        accDetailsReq.setAlias(rq.getFromAcc().length() > 10);
                        AccountDetailBankResponse debitBankResp =
                                coreQueryClient.getDDAccountDetails(accDetailsReq);
                        if (!"0".equals(debitBankResp.getResponseStatus().getResCode())) {
                            String code = "0199";
                            if (debitBankResp.getResponseStatus().getIsFail()) {
                                code = debitBankResp.getResponseStatus().getResCode();
                            }
                            log.info("Failed to get debit account details");
                            rp.setCode(code);
                            rp.setMessage(commonService.getMessage("ACC-DETAIL-" + code, rq.getLang()));
                            return rp;
                        }

                        if (!vn.vnpay.commoninterface.common.Constants.ALLOWED_ACC_STT_DEBIT.contains(
                                debitBankResp.getAccountStatus())) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }

                        String serviceCode = Constants.ServiceCode.CASH_TRANS;
                        // Lấy thông tin service type
                        Optional<MbService> mbServiceOpt = mbServiceRepository.findByServiceCode(serviceCode);
                        if (!mbServiceOpt.isPresent()) {
                            log.info("Invalid service code: {}", serviceCode);
                            rp.setCode(Constants.ResCode.ERROR_96);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return rp;
                        }

                        // Kiểm tra quyền giao dịch tài khoản nguồn
                        rp = transactionService.validateTransAuthorityForAccount(rp, user, rq.getFromAcc(), serviceCode, rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }
                        // Kiểm tra mã sản phẩm tài khoản
                        rp =
                                transactionService.validateAccountProduct(
                                        rp,
                                        serviceCode,
                                        debitBankResp.getProductCode(),
                                        null,
                                        rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }
                        // Check role type được quyền thao tác chức năng hay không
                        rp =
                                transactionService.validateUserAndServiceCode(
                                        rp, user, serviceCode, "1", rq.getLang(), null);
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        // Lấy phương thức xác thực
                        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
                        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
                            rp.setCode(checkAuthen.getCode());
                            rp.setMessage(commonService.getMessage(checkAuthen.getCode(), rq.getLang()));
                            return rp;
                        }

                        String authenType = checkAuthen.getAuthenMethod();
                        dataRp.setAuthenType(authenType);
                        log.info("Maker authen type: {}", authenType);

                        // Lấy tỉ giá ngoại tệ
                        BigDecimal rate = BigDecimal.ONE; // VND
                        BigDecimal rateUSD = BigDecimal.ONE;
                        if (!"VND".equalsIgnoreCase(debitBankResp.getCurCode())) {
                            ExchangeRateInquiryBankRequest bankReq =
                                    ExchangeRateInquiryBankRequest.builder()
                                            .currency(debitBankResp.getCurCode())
                                            .build();
                            ExchangeRateInquiryBankResponse bankResp =
                                    coreQueryClient.getExchangeRateInquiry(bankReq);
                            if (!bankResp.getResponseStatus().getIsSuccess()) {
                                log.info("Failed to get exchange rate");
                                rp.setCode(bankResp.getResponseStatus().getResCode());
                                rp.setMessage(bankResp.getResponseStatus().getResMessage());
                                return rp;
                            }
                            rate = bankResp.getAppXferBuy();
                            if ("USD".equals(debitBankResp.getCurCode())) {
                                rateUSD = rate;
                            } else {
                                bankResp =
                                        coreQueryClient.getExchangeRateInquiry(
                                                ExchangeRateInquiryBankRequest.builder().currency("USD").build());
                                if (bankResp.getResponseStatus().getIsFail()) {
                                    rp.setCode(bankResp.getResponseStatus().getResCode());
                                    rp.setMessage(
                                            commonService.getMessage(
                                                    bankResp.getResponseStatus().getResCode(), rq.getLang()));
                                    return rp;
                                }
                                rateUSD = bankResp.getAppXferBuy();
                            }
                        }

                        // Quy đổi ngoại tệ <-> VND
                        BigDecimal amountVND, originAmount;
                        if ("VND".equalsIgnoreCase(rq.getCurCode())) {
                            amountVND = new BigDecimal(rq.getAmount().replace(",", "")).setScale(0, RoundingMode.HALF_UP);
                            originAmount = amountVND.divide(rate, 2, RoundingMode.HALF_UP);
                        } else {
                            originAmount = new BigDecimal(rq.getAmount().replace(",", ""));
                            amountVND = originAmount.multiply(rate).setScale(0, RoundingMode.HALF_UP);
                        }

                        // Tính phí giao dịch
                        String vatExamptFlag = commonService.getVatExemptFlag(rq);
                        GetFeeTransferDTO input = GetFeeTransferDTO.builder()
                                .accountPkgCode(user.getAccountPkgCode())
                                .pkgCode(user.getPackageCode())
                                .promCode(user.getValidPromCode())
                                .serviceCode(serviceCode)
                                .authMethod(authenType)
                                .ccy(accPreView.getCurCode())
                                .amount(originAmount)
                                .exchangeAmount(amountVND)
                                .isExamptVat("Y".equalsIgnoreCase(vatExamptFlag))
                                .creditAccount(rq.getId())
                                .build();
                        FeeTransferDTO feeDto = transactionFeeService.getFeeTransfer(input);
                        BigDecimal feeAmt, feeVat, originFeeAmt, originFeeVat, feeU, vatU;
                        if ("VND".equals(feeDto.getCcy())) {
                            feeAmt = feeDto.getFee();
                            feeVat = feeDto.getVat();
                            originFeeAmt = feeAmt.divide(rate, 2, RoundingMode.HALF_UP);
                            originFeeVat = feeVat.divide(rate, 2, RoundingMode.HALF_UP);
                            feeU = vatU = null;
                        } else {
                            feeU = feeDto.getFee();
                            vatU = feeDto.getVat();
                            feeAmt = feeU.multiply(rateUSD);
                            feeVat = vatU.multiply(rateUSD);
                            originFeeAmt = feeAmt.divide(rate, 2, RoundingMode.HALF_UP);
                            originFeeVat = feeVat.divide(rate, 2, RoundingMode.HALF_UP);
                        }
                        BigDecimal debitAmount, creditAmount, originDebitAmt;
                        if ("1".equals(rq.getFeeType())) {
                            debitAmount = amountVND.add(feeAmt).add(feeVat).setScale(2, RoundingMode.HALF_UP);
                            originDebitAmt = originAmount.add(originFeeAmt).add(originFeeVat).setScale(2, RoundingMode.HALF_UP);
                            creditAmount = amountVND;
                        } else {
                            debitAmount = amountVND;
                            originDebitAmt = originAmount;
                            creditAmount = amountVND.subtract(feeAmt).subtract(feeVat);
                        }

                        log.info("Rate: {}", rate.doubleValue());
                        log.info("originAmount: {}", originAmount.doubleValue());
                        log.info("amountVND: {}", amountVND);
                        log.info("feeAmt: {}", feeAmt.doubleValue());
                        log.info("feeVat: {}", feeVat.doubleValue());
                        log.info("originFeeAmt: {}", originFeeAmt.doubleValue());
                        log.info("originFeeVat: {}", originFeeVat.doubleValue());
                        log.info("debitAmount: {}", debitAmount.doubleValue());
                        log.info("originDebitAmt: {}", originDebitAmt.doubleValue());
                        log.info("creditAmount: {}", creditAmount.doubleValue());

                        // Bổ sung thông tin vào metadata
                        metadata.setTotalFee(originFeeAmt.doubleValue() + originFeeVat.doubleValue());
                        metadata.setExchangeTotalFee(feeAmt.doubleValue() + feeVat.doubleValue());
                        metadata.setAmountVND(amountVND.longValue());
                        metadata.setOriginAmount(originAmount.doubleValue());
                        metadata.setFeeU(feeU);
                        metadata.setVatU(vatU);
                        metadata.setClientRqCcy(rq.getCurCode());
                        metadata.setVatExamptFlag(vatExamptFlag);
                        metadata.setDebitName(debitBankResp.getAccountName());
                        metadata.setDebitAddr(debitBankResp.getAccountAddress());
                        metadata.setIdIssuedPlace(rq.getIdIssuedPlace());

                        // Kiểm tra giao dịch đi thẳng
                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(), user.getUsername(), serviceCode, rq.getFromAcc());
                        log.info("isExecTrans? {}", isExecTrans);

                        // Kiểm tra số dư tại khoản debit
                        String minBal =
                                debitBankResp.getCurCode().equals("VND")
                                        ? commonService.getConfig("MIN_BALANCE", "50000")
                                        : commonService.getConfig("MIN_BALANCE_FOR", "10");

                        if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                .subtract(new BigDecimal(minBal))
                                .doubleValue()
                                < originDebitAmt.doubleValue()) {
                            if (isExecTrans) {
                                rp.setCode(Constants.ResCode.ERROR_112);
                                rp.setMessage(
                                        commonService.getMessage(Constants.MessageCode.ERROR_112, rq.getLang()));
                                return rp;
                            } else {
                                if (!"1".equals(rq.getIsByPassNotBalance())) {
                                    rp.setCode(Constants.ResCode.INFO_45);
                                    rp.setMessage(
                                            commonService.getMessage(Constants.MessageCode.INFO_45, rq.getLang()));
                                    return rp;
                                }
                            }
                        }

                        if (creditAmount.longValue() < 0) {
                            log.info("creditAmount < 0");
                            rp.setCode(Constants.ResCode.ERROR_113);
                            rp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_113, rq.getLang()));
                            return rp;
                        }

                        // Convert tiền tệ khác USD, và VND để tính toàn hạn mức
                        CheckLimitTrans checkLimitTrans =
                                transactionLimitService.convertCcyAmount(
                                        rp, accPreView.getCurCode(), originAmount, amountVND);
                        if (!Constants.ResCode.INFO_00.equals(rp.getCode())) {
                            return rp;
                        }

                        // Kiểm tra hạn mức giao dịch chung
                        metadata.setCheckLimitTrans(checkLimitTrans);
                        metadata.setExecTrans(isExecTrans);
                        BaseClientResponse checkLimit;

                        checkLimit =
                                transactionLimitService.checkTranLimit(
                                        user,
                                        rq,
                                        serviceCode,
                                        checkLimitTrans.getAmount(),
                                        checkLimitTrans.getCcy(),
                                        authenType,
                                        isExecTrans);
                        if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                            log.error("Lỗi hạn mức");
                            return checkLimit;
                        }

                        // Build Trans object
                        long transId = smeTransRepository.getNextValSmeTransSeq().longValue();
                        SmeTrans smeTrans = new SmeTrans();
                        smeTrans.setId(transId);
                        smeTrans.setTranxContent(rq.getContent());
                        smeTrans.setMakerAuthenType(authenType);
                        smeTrans.setCusName(user.getCusName());
                        smeTrans.setCreatedUser(user.getUsername());
                        smeTrans.setCreatedMobile(user.getMobileOtp());
                        smeTrans.setFromAcc(rq.getFromAcc());
                        smeTrans.setToAcc(rq.getId());
                        smeTrans.setTranxType(serviceCode);
                        smeTrans.setTranxNote("Maker init");
                        smeTrans.setCifNo(user.getCif());
                        smeTrans.setTranxTime(LocalDateTime.now());
                        smeTrans.setServiceType(mbServiceOpt.get().getServiceType());
                        smeTrans.setCcy(accPreView.getCurCode());
                        smeTrans.setStatus(Constants.TransStatus.MAKER_WAIT_CONFIRM);
                        smeTrans.setFeeType(rq.getFeeType());
                        smeTrans.setCreditName(debitBankResp.getAccountName());
                        smeTrans.setBeneBankCode("970436");
                        smeTrans.setBeneBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setBranchCode(user.getBranchCode());
                        smeTrans.setChannel(rq.getSource());
                        smeTrans.setFeeOnAmt(feeVat.doubleValue());
                        smeTrans.setFlatFee(feeAmt.doubleValue());
                        smeTrans.setAmount(originAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());
                        smeTrans.setTotalAmount(amountVND.longValue());
                        smeTrans.setDebitBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setCifInt(user.getCifInt());
                        smeTrans.setRealAmount(debitAmount);

                        // Tài khoản nguồn
                        DebitAccountDTO debitAccount =
                                DebitAccountDTO.builder()
                                        .cif(user.getCifInt())
                                        .accountNo(accPreView.getAccountNo())
                                        .accountAlias(accPreView.getAccountAlias())
                                        .accountType(accPreView.getAccountType())
                                        .accountHolderName(debitBankResp.getAccountName())
                                        .amountVND(debitAmount.longValue())
                                        .originAmount(originDebitAmt.doubleValue())
                                        .currency(accPreView.getCurCode())
                                        .branch(debitBankResp.getBranchNo())
                                        .rate(String.valueOf(rate))
                                        .build();

                        // Tài khoản đích
                        CreditAccountDTO creditAccount =
                                CreditAccountDTO.builder()
//                                        .cif(Integer.parseInt(creditBankResp.getCif()))
                                        .accountNo(rq.getId())
                                        .accountAlias(null)
                                        .accountType(null)
                                        .accountHolderName(rq.getFullname())
                                        .branch(null)
                                        .amountVND(creditAmount.longValue())
                                        .originAmount(creditAmount.longValue())
                                        .currency("VND")
                                        .accountType(null)
                                        .rate("1")
                                        .build();

                        FeeDTO fee =
                                FeeDTO.builder()
                                        .amount(feeAmt.doubleValue())
                                        .authMethod(0)
                                        .currency(accPreView.getCurCode())
                                        .originAmount(originFeeAmt.doubleValue())
                                        .originAuthMethod(0.0)
                                        .originVatAmount(originFeeVat.doubleValue())
                                        .type(Integer.parseInt(rq.getFeeType()))
                                        .vatAmount(feeVat.doubleValue())
                                        .build(); // Phí

                        RecipientDTO recipient =
                                RecipientDTO.builder()
                                        .fullname(rq.getFullname())
                                        .idType(rq.getIdType())
                                        .id(rq.getId())
                                        .issuedDate(rq.getIssuedDate())
                                        .issuedPlace(rq.getIssuedPlace())
                                        .build();
                        metadata.setDebitAccount(debitAccount);
                        metadata.setCreditAccount(creditAccount);
                        metadata.setFee(fee);
                        metadata.setRecipient(recipient);
                        smeTrans.setMetadata(gson.toJson(metadata));
                        //build remark
                        remark.append(transId)
                                .append(".").append(rq.getIdType())
                                .append(".").append(rq.getId())
                                .append(".").append(CommonUtils.TimeUtils.format("yyyy-MM-dd", "dd/MM/yyyy", rq.getIssuedDate()))
                                .append(".").append(CommonUtils.removeAccent(rq.getIssuedPlace()))
                                .append(".").append(rq.getFullname());
                        if (StringUtils.isNotBlank(rq.getContent()))
                            remark.append(".").append(rq.getContent());
                        String remarkStr = remark.toString();
                        log.info("Remark: {}", remarkStr);
                        smeTrans.setTranxRemark(remarkStr);
                        smeTrans = smeTransRepository.save(smeTrans);
                        // Lưu thêm chi tiết giao dịch
                        SmeTransactionDetail transactionDetail = new SmeTransactionDetail();
                        transactionDetail.setTranxId(smeTrans.getId());
                        transactionDetail.setTranxPhase(Constants.TransPhase.MAKER_INIT);
                        transactionDetail.setResCode(Constants.ResCode.INFO_00);
                        transactionDetail.setResDesc("Success");
                        transactionDetail.setTranxNote("Maker init");
                        transactionDetail.setDetail("Maker init success");
                        transactionDetail.setCreatedDate(LocalDateTime.now());
                        transactionDetail.setSource(rq.getSource());

                        // Khởi tạo phương thức xác thực
                        String tranToken = transactionService.genTranToken();
                        try {
                            InitAuthenResponse intAuthen =
                                    transactionService.intAuthen(
                                            user,
                                            rq,
                                            authenType,
                                            rq.getFromAcc(),
                                            rq.getId(),
                                            smeTrans.getId(),
                                            rq.getAmount(),
                                            tranToken,
                                            serviceCode,
                                            "",
                                            rq.getCurCode());
                            if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                                log.error("init authen error: " + intAuthen.getCode());

                                transactionDetail.setResCode(intAuthen.getCode());
                                transactionDetail.setResDesc(intAuthen.getDataAuthen());
                                transactionDetail.setTranxNote("Maker init failed");
                                transactionDetail.setDetail("Init authen method failed");
                                smeTransDetailRepository.save(transactionDetail);

                                rp.setCode(intAuthen.getCode());
                                rp.setMessage(intAuthen.getMessage());
                                return rp;
                            }
                            smeTrans.setChallenge(intAuthen.getDataAuthen());
                            smeTrans.setAuthenType(authenType);
                            dataRp.setTranToken(tranToken);
                            dataRp.setDataAuthen(intAuthen.getDataAuthen());
                        } catch (Exception e) {
                            log.info("Error: ", e);

                            smeTrans.setTranxNote("Khoi tao ptxt that bai");
                            smeTransRepository.save(smeTrans);

                            transactionDetail.setResCode(Constants.ResCode.ERROR_96);
                            transactionDetail.setResDesc("Error");
                            transactionDetail.setTranxNote("Maker init failed");
                            transactionDetail.setDetail("Init authen method failed");

                            rp.setCode(Constants.ResCode.ERROR_96);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return rp;
                        }

                        smeTransDetailRepository.save(transactionDetail);

                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        // Trả ra dữ liệu cho client
                        dataRp.setFee(originFeeAmt.doubleValue());
                        dataRp.setVat(originFeeVat.doubleValue());
                        dataRp.setTotalFee(originFeeAmt.add(originFeeVat).doubleValue());
                        dataRp.setClientRqCcy(rq.getCurCode());

                        if ("VND".equals(feeDto.getCcy())) {
                            dataRp.setExchangeFee(feeAmt);
                            dataRp.setExchangeVat(feeVat);
                            dataRp.setExchangeTotalFee(feeAmt.add(feeVat));
                        }
                        if (feeU != null) {
                            dataRp.setFeeU(feeU);
                            dataRp.setVatU(vatU);
                            dataRp.setTotalFeeU(feeU.add(vatU));
                        }

                        dataRp.setTotalAmount(originDebitAmt.doubleValue());
                        dataRp.setExchangeTotalAmount(debitAmount.longValue());
                        dataRp.setAmount(originAmount.doubleValue());
                        dataRp.setExchangeAmount(amountVND.longValue());
                        dataRp.setToAccName(rq.getFullname());
                        dataRp.setServiceCode(smeTrans.getTranxType());
                        dataRp.setFeeToShow(CommonUtils.formatAmount(dataRp.getFee(), fee.getCurrency()));
                        dataRp.setVatToShow(CommonUtils.formatAmount(dataRp.getVat(), fee.getCurrency()));
                        dataRp.setTotalFeeToShow(
                                CommonUtils.formatAmount(dataRp.getTotalFee(), fee.getCurrency()));
                        dataRp.setTranxId(String.valueOf(smeTrans.getId()));


                        rp.setData(dataRp);
                        return rp;

                    default:
                        log.info("Invalid user status");
                        rp.setCode(Constants.ResCode.USER_100);
                        rp.setMessage(commonService.getMessage(Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                rp.setCode(Constants.ResCode.USER_404);
                rp.setMessage(commonService.getMessage(Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return rp;
    }

    public BaseClientResponse makerConfirmCashPayment(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        boolean isDeleteCache = true;
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(req);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        log.info("BaseConfirmRq: {}", gson.toJson(req));
                        // Kiểm tra giao dịch có đang xử lý
                        boolean isExe = !redisCacheService.setnx(req.getTranxId(), 15l);
                        if (isExe) {
                            log.error("trans duplicate");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }
                        // Valid giao dich
                        BaseClientResponse valid = transactionService.validTxn(req);
                        if (!Constants.ResCode.INFO_00.equals(valid.getCode())) {
                            return valid;
                        }
                        // Get cache value
                        String cacheTrans = redisCacheService.getTxn(req);
                        log.info("Cache transaction: " + cacheTrans);
                        SmeTrans cachedSmeTrans = gson.fromJson(cacheTrans, SmeTrans.class);

                        if (cachedSmeTrans.isRequestProcessed()) {
                            log.error("trans dup");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }

                        if (!Constants.TransStatus.MAKER_WAIT_CONFIRM.equals(cachedSmeTrans.getStatus())) {
                            log.info("Invalid trans status: {}", cachedSmeTrans.getStatus());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        if (!user.getUsername().equals(cachedSmeTrans.getCreatedUser())) {
                            log.info("Created user ({}) and request user ({}) are not the same", cachedSmeTrans.getCreatedUser(), user.getUsername());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        if (!cachedSmeTrans.getMakerAuthenType().equals(req.getAuthenType())) {
                            log.info("Invalid authenType: {}", req.getAuthenType());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INVALID_DATA, req.getLang()));
                            return resp;
                        }

                        String bankName = "";
                        if (!Strings.isNullOrEmpty(cachedSmeTrans.getMetadata())) {
                            TransactionMetaDataDTO metaDataDTO =
                                    gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);
                            bankName = metaDataDTO.getBeneBankName();
                        }

                        if (!user.getCif().equals(cachedSmeTrans.getCifNo())) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_96,
                                    commonService.getMessage(Constants.MessageCode.INVALID_DATA, req.getLang()));
                        }

                        // Kiếm tra role type được quyền thực hiện chức năng hay không
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, cachedSmeTrans.getTranxType(), "1", req.getLang(), null);
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Cap nhat chi tiet giao dich
                        SmeTransactionDetail transactionDetail = new SmeTransactionDetail();
                        transactionDetail.setTranxId(cachedSmeTrans.getId());
                        transactionDetail.setTranxPhase(Constants.TransPhase.MAKER_CONFIRM);
                        transactionDetail.setResCode("00");
                        transactionDetail.setCreatedDate(LocalDateTime.now());
                        transactionDetail.setTranxNote("Thanh cong");
                        transactionDetail.setSource(req.getSource());

                        // Xác thực OTP
                        InitAuthenResponse initAuthenResponse =
                                transactionService.confirmAuthenTxn(
                                        user,
                                        req,
                                        cachedSmeTrans.getAuthenType(),
                                        cachedSmeTrans.getId(),
                                        req.getAuthenValue(),
                                        req.getTranToken(),
                                        Strings.nullToEmpty(cachedSmeTrans.getChallenge()),
                                        String.valueOf(cachedSmeTrans.getAmount()),
                                        cachedSmeTrans.getServiceCode(),
                                        cachedSmeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(initAuthenResponse.getCode())) {
                            resp.setCode(initAuthenResponse.getCode());
                            resp.setMessage(initAuthenResponse.getMessage());

                            transactionDetail.setResCode(initAuthenResponse.getCode());
                            transactionDetail.setResDesc(initAuthenResponse.getMessage());
                            transactionDetail.setTranxNote("Maker confirm failed");
                            transactionDetail.setDetail("Authenticate failed");
                            smeTransDetailRepository.save(transactionDetail);
                            return resp;
                        }
                        // Kiểm tra giao dịch đi ngay hay chờ duyệt
                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(),
                                        user.getUsername(),
                                        cachedSmeTrans.getTranxType(),
                                        cachedSmeTrans.getFromAcc());
                        log.info("=====: " + cachedSmeTrans.getBranchCode());
                        // Xử lý lưu giao dich để check hạn mức
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        String type;
                        if (isExecTrans) {
                            resp =
                                    transactionService.execCashTransfer(
                                            resp, cachedSmeTrans, req);
                            if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                                transactionDetail.setResCode(resp.getCode());
                                transactionDetail.setTranxNote(resp.getMessage());
                                transactionDetail.setDetail(resp.getMessage());
                                smeTransDetailRepository.save(transactionDetail);

                                return resp;
                            }
                            type = "2";
                        } else {
                            cachedSmeTrans.setStatus(Constants.TransStatus.MAKER_SUCCESS);
                            cachedSmeTrans.setTranxNote("Lập lệnh thành công");
                            type = "1";
                            smeTransRepository.save(cachedSmeTrans);
                        }

                        smeTransDetailRepository.save(transactionDetail);

                        transactionLimitService.saveCheckTransLimit(
                                user,
                                req,
                                cachedSmeTrans.getTranxType(),
                                metadata.getCheckLimitTrans().getAmount(),
                                metadata.getCheckLimitTrans().getCcy(),
                                req.getAuthenType(),
                                type);
                        // end
                        smeTransRepository.save(cachedSmeTrans);

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranxId(String.valueOf(cachedSmeTrans.getId()));
                        dataRp.setTranDate(CommonUtils.formatDate(new Date()));
                        dataRp.setIsExecTrans(isExecTrans ? "1" : "0");

                        // lưu chức năng gần đây
                        commonService.saveFuncRecent(cachedSmeTrans.getTranxType(),
                                user.getUsername(),
                                user.getRoleType(),
                                user.getConfirmType(),
                                req.getSource());

                        boolean isContact =
                                commonService.isSavedBene(
                                        cachedSmeTrans.getTranxType(),
                                        cachedSmeTrans.getBeneBankCode(),
                                        req.getUser(),
                                        null,
                                        metadata.getRecipient().getIdType(),
                                        metadata.getRecipient().getId(),
                                        null);

                        // return transaction info
                        String metaStr = cachedSmeTrans.getMetadata();
                        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);
                        dataRp.setAmount(metaData.getOriginAmount());
                        dataRp.setExchangeAmount((long) metaData.getAmountVND());
                        dataRp.setClientRqCcy(metaData.getClientRqCcy());
                        dataRp.setContact(isContact);

                        resp.setData(dataRp);
                        cachedSmeTrans.setRequestProcessed(true);
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);
                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, req.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, req.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            log.info("Error confim: ", e);
        } finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse checkerInitCashPayment(BaseCheckerInitRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        SmeTrans smeTrans = smeTransRepository.findById(Long.parseLong(rq.getTranxId())).get();
                        if (!smeTrans.getCifNo().equals(user.getCif())) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_96,
                                    commonService.getMessage(Constants.MessageCode.INVALID_DATA, rq.getLang()));
                        }
                        if (smeTrans == null
                                || (!"5".equals(smeTrans.getStatus()) && !"10".equals(smeTrans.getStatus()))) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_0204,
                                    commonService.getMessage(Constants.MessageCode.CONFIRM_TRANS_FAIL, rq.getLang()));
                        }
                        TransactionMetaDataDTO transactionMetaDataDTO =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);

                        // Kiểm tra tài khoản debit
                        List<AccountDTO> listAccount = user.getListAccount();
                        if (listAccount.isEmpty()) {
                            AccountListingBankRequest accListReq = new AccountListingBankRequest();
                            accListReq.setAccountGroupType("ALL");
                            accListReq.setCif(user.getCif());
                            accListReq.setJoinable(true);
                            AccountListingBankResponse accountListingResponse =
                                    coreQueryClient.getAccountListByCif(accListReq);

                            if (!accountListingResponse.getResponseStatus().getIsSuccess()) {
                                log.info("Failed to query account list");
                            }

                            listAccount =
                                    modelMapper.map(
                                            accountListingResponse.getListAccount(),
                                            new TypeToken<List<AccountDTO>>() {
                                            }.getType());

                            user.setListAccount(listAccount);
                            redisCacheService.putSession(rq, user);
                        }
                        log.info("List account: " + listAccount);

                        SmeTrans finalSmeTrans = smeTrans;
                        Optional<AccountDTO> opt =
                                listAccount.stream()
                                        .filter(
                                                x ->
                                                        (finalSmeTrans.getFromAcc().equalsIgnoreCase(x.getAccountNo())
                                                                || finalSmeTrans
                                                                .getFromAcc()
                                                                .equalsIgnoreCase(x.getAccountAlias())))
                                        .findFirst();

                        if (!opt.isPresent()) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }
                        AccountDTO acc = opt.get();

                        log.info("====== " + smeTrans.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));

                        // Lấy thông tin mã sản phẩm tài khoản debit
                        log.info("Get Debit account details for {}", smeTrans.getFromAcc());
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(smeTrans.getFromAcc());
                        accDetailsReq.setAccountType("D");
                        accDetailsReq.setAlias(smeTrans.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));
                        AccountDetailBankResponse debitBankResp =
                                coreQueryClient.getDDAccountDetails(accDetailsReq);
                        if (!debitBankResp.getResponseStatus().getIsSuccess()) {
                            log.info("Failed to get debit account details");
                            rp.setCode(debitBankResp.getResponseStatus().getResCode());
                            rp.setMessage(debitBankResp.getResponseStatus().getResMessage());
                            return rp;
                        }

                        // Kiểm tra trạng thái tài khoản debit
                        if (!vn.vnpay.commoninterface.common.Constants.ALLOWED_ACC_STT_DEBIT.contains(
                                debitBankResp.getAccountStatus())) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }

                        rp =
                                transactionService.checkDebitAccountStatus(
                                        rp,
                                        smeTrans.getFromAcc(),
                                        acc.getAccountType(),
                                        smeTrans.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()),
                                        true,
                                        rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        String minBal =
                                debitBankResp.getCurCode().equals("VND")
                                        ? commonService.getConfig("MIN_BALANCE", "50000")
                                        : commonService.getConfig("MIN_BALANCE_FOR", "10");

                        if (transactionMetaDataDTO != null
                                && transactionMetaDataDTO.getDebitAccount() != null) {
                            if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                    .subtract(new BigDecimal(minBal))
                                    .doubleValue()
                                    < transactionMetaDataDTO.getDebitAccount().getOriginAmount()) {
                                rp.setCode(Constants.ResCode.ERROR_112);
                                rp.setMessage(
                                        commonService.getMessage(Constants.MessageCode.ERROR_112, rq.getLang()));
                                return rp;
                            }
                        }

                        // Check role type được quyền thao tác chức năng hay không
                        rp =
                                transactionService.validateUserAndServiceCode(
                                        rp,
                                        user,
                                        smeTrans.getTranxType(),
                                        "2",
                                        rq.getLang(),
                                        smeTrans.getCreatedUser());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        // Lấy ptxt
                        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
                        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
                            rp.setCode(checkAuthen.getCode());
                            rp.setMessage(commonService.getMessage(checkAuthen.getCode(), rq.getLang()));
                            return rp;
                        }

                        // Kiểm tra hạn mức giao dịch chung
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        if (metadata != null) {
                            // Kiểm tra hạn mức giao dịch chung
                            BaseClientResponse checkLimit =
                                    transactionLimitService.checkTranLimit(
                                            user,
                                            rq,
                                            smeTrans.getTranxType(),
                                            metadata.getCheckLimitTrans().getAmount(),
                                            metadata.getCheckLimitTrans().getCcy(),
                                            checkAuthen.getAuthenMethod(),
                                            true);
                            if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                                log.error("Lỗi hạn mức");
                                return checkLimit;
                            }
                        }

                        // Khởi tạo ptxt
                        String tranToken = transactionService.genTranToken();
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTrans.getFromAcc(),
                                        transactionMetaDataDTO.getRecipient().getId(),
                                        smeTrans.getId(),
                                        String.valueOf(transactionMetaDataDTO.getAmountVND()),
                                        tranToken,
                                        smeTrans.getTranxType(),
                                        "",
                                        smeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                            log.error("init authen error: " + intAuthen.getCode());
                            rp.setCode(intAuthen.getCode());
                            rp.setMessage(intAuthen.getMessage());
                            return rp;
                        }

                        // Cập nhật trạng thái giao dịch
                        smeTrans.setStatus(Constants.TransStatus.CHEKER_WAIT_CONFIRM);
                        smeTrans.setTranxNote("Khởi tạo duyệt lệnh thành công");
                        smeTrans.setReason(rq.getReason());
                        smeTrans.setApprovedUser(rq.getUser());
                        smeTrans.setAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans.setChallenge(intAuthen.getDataAuthen());
                        smeTrans = smeTransRepository.save(smeTrans);

                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranToken(tranToken);
                        dataRp.setDataAuthen(intAuthen.getDataAuthen());
                        dataRp.setFromAccount(smeTrans.getFromAcc());
                        dataRp.setToAccount(transactionMetaDataDTO.getRecipient().getId());
                        dataRp.setAmount(transactionMetaDataDTO.getAmountVND());

                        String authenType = checkAuthen.getAuthenMethod();
                        dataRp.setAuthenType(authenType);
                        log.info("Checker authen type: {}", authenType);

                        rp.setData(dataRp);

                        return rp;

                    default:
                        log.info("Invalid user status");
                        rp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        rp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                rp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                rp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return rp;
    }

    public BaseClientResponse checkerConfirmCashPayment(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        boolean isDeleteCache = true;
        try {
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(req);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Kiểm tra giao dịch có đang xử lý
                        boolean isExe = !redisCacheService.setnx(req.getTranxId(), 15l);
                        if (isExe) {
                            log.error("trans duplicate");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }
                        // Valid giao dich
                        BaseClientResponse valid = transactionService.validTxn(req);
                        if (!Constants.ResCode.INFO_00.equals(valid.getCode())) {
                            return valid;
                        }
//                        SmeTrans smeTrans = smeTransRepository.findById(Long.parseLong(req.getTranxId())).get();

                        // Get cache value
                        SmeTrans cachedSmeTrans = gson.fromJson(redisCacheService.getTxn(req), SmeTrans.class);

                        SmeTrans smeTrans = smeTransRepository.findById(cachedSmeTrans.getId()).get();

                        if (cachedSmeTrans.isRequestProcessed()) {
                            log.error("trans dup");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }

                        if (smeTrans == null
                                || (!"5".equals(smeTrans.getStatus()) && !"10".equals(smeTrans.getStatus()))) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_0204,
                                    commonService.getMessage(
                                            Constants.MessageCode.CONFIRM_TRANS_FAIL, req.getLang()));
                        }


                        if (!smeTrans.getCifNo().equals(user.getCif())
                                || cachedSmeTrans.getId() != Long.parseLong(req.getTranxId())) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_96,
                                    commonService.getMessage(
                                            Constants.MessageCode.INVALID_DATA, req.getLang()));
                        }

                        // Kiếm tra role type được quyền thực hiện chức năng hay không
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, cachedSmeTrans.getTranxType(), "2", req.getLang(), smeTrans.getCreatedUser());
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Cap nhat chi tiet giao dich
                        SmeTransactionDetail transactionDetail = new SmeTransactionDetail();
                        transactionDetail.setTranxId(cachedSmeTrans.getId());
                        transactionDetail.setTranxPhase(Constants.TransPhase.CHECKER_CONFIRM);
                        transactionDetail.setResCode("00");
                        transactionDetail.setCreatedDate(LocalDateTime.now());
                        transactionDetail.setTranxNote("Thanh cong");
                        transactionDetail.setSource(req.getSource());

                        // Xác thực OTP
                        InitAuthenResponse initAuthenResponse =
                                transactionService.confirmAuthenTxn(
                                        user,
                                        req,
                                        cachedSmeTrans.getAuthenType(),
                                        cachedSmeTrans.getId(),
                                        req.getAuthenValue(),
                                        req.getTranToken(),
                                        Strings.nullToEmpty(cachedSmeTrans.getChallenge()),
                                        String.valueOf(cachedSmeTrans.getAmount()),
                                        cachedSmeTrans.getTranxType(),
                                        cachedSmeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(initAuthenResponse.getCode())) {
                            transactionDetail.setResCode(initAuthenResponse.getCode());
                            transactionDetail.setResDesc(initAuthenResponse.getMessage());
                            transactionDetail.setTranxNote("Checker confirm failed");
                            transactionDetail.setDetail("Authenticate failed");
                            smeTransDetailRepository.save(transactionDetail);

                            resp.setCode(initAuthenResponse.getCode());
                            resp.setMessage(initAuthenResponse.getMessage());
                            return resp;
                        }

                        // Thực hiện lệnh chuyển khoản
                        resp = transactionService.execCashTransfer(resp, cachedSmeTrans, req);
                        //            if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                        //              return resp;
                        //            }
                        log.info(gson.toJson(resp));
                        TransactionMetaDataDTO transactionMetaDataDTO =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        // log.info(gson.toJson(transactionMetaDataDTO));
                        TransConfirmRes transConfirmRes =
                                TransConfirmRes.builder()
                                        .tranxId(String.valueOf(cachedSmeTrans.getId()))
                                        .tranxDate(CommonUtils.getDate("HH:mm dd/MM/yyyy"))
                                        .fee(
                                                transactionMetaDataDTO.getFee() != null
                                                        ? transactionMetaDataDTO.getFee().getOriginAmount()
                                                        : transactionMetaDataDTO
                                                        .getFutureTransData()
                                                        .getFee()
                                                        .getOriginAmount())
                                        .exchangeFee(
                                                transactionMetaDataDTO.getFee() != null
                                                        ? transactionMetaDataDTO.getFee().getAmount()
                                                        : transactionMetaDataDTO.getFutureTransData().getFee().getAmount())
                                        .vat(
                                                transactionMetaDataDTO.getFee() != null
                                                        ? transactionMetaDataDTO.getFee().getOriginVatAmount()
                                                        : transactionMetaDataDTO
                                                        .getFutureTransData()
                                                        .getFee()
                                                        .getOriginVatAmount())
                                        .exchangeVat(
                                                transactionMetaDataDTO.getFee() != null
                                                        ? transactionMetaDataDTO.getFee().getVatAmount()
                                                        : transactionMetaDataDTO.getFutureTransData().getFee().getVatAmount())
                                        .totalAmount(
                                                transactionMetaDataDTO.getOriginAmount()
                                                        + transactionMetaDataDTO.getTotalFee())
                                        .exchangeTotalAmount(
                                                transactionMetaDataDTO.getAmountVND()
                                                        + transactionMetaDataDTO.getExchangeTotalFee())
                                        .amount(transactionMetaDataDTO.getOriginAmount())
                                        .exchangeAmount(transactionMetaDataDTO.getAmountVND())
                                        .isExecTrans("0")
                                        .isContact(
                                                commonService.isSavedBene(
                                                        cachedSmeTrans.getTranxType(),
                                                        cachedSmeTrans.getBeneBankCode(),
                                                        req.getUser(),
                                                        null,
                                                        transactionMetaDataDTO.getRecipient().getIdType(),
                                                        transactionMetaDataDTO.getRecipient().getId(),
                                                        null))
                                        .build();
                        if (transactionMetaDataDTO.getFee() != null) {
                            transConfirmRes.setTotalFee(
                                    transactionMetaDataDTO.getFee().getOriginAmount()
                                            + transactionMetaDataDTO.getFee().getOriginVatAmount());
                            transConfirmRes.setExchangeTotalFee(
                                    transactionMetaDataDTO.getFee().getAmount()
                                            + transactionMetaDataDTO.getFee().getVatAmount());
                        } else {
                            transConfirmRes.setTotalFee(
                                    transactionMetaDataDTO.getFutureTransData().getFee().getOriginAmount()
                                            + transactionMetaDataDTO.getFutureTransData().getFee().getOriginVatAmount());
                            transConfirmRes.setExchangeTotalFee(
                                    transactionMetaDataDTO.getFutureTransData().getFee().getAmount()
                                            + transactionMetaDataDTO.getFutureTransData().getFee().getVatAmount());
                        }

                        resp.setData(transConfirmRes);

                        commonService.saveFuncRecent(cachedSmeTrans.getTranxType(),
                                user.getUsername(),
                                user.getRoleType(),
                                user.getConfirmType(),
                                req.getSource());

                        // Luu check han muc
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);

                        transactionLimitService.saveCheckTransLimit(
                                user,
                                req,
                                cachedSmeTrans.getTranxType(),
                                metadata.getCheckLimitTrans().getAmount(),
                                metadata.getCheckLimitTrans().getCcy(),
                                req.getAuthenType(),
                                "2");


                        if ("00".equals(resp.getCode()) || "0".equals(resp.getCode())) {
                            transactionDetail.setResCode(resp.getCode());
                            transactionDetail.setResDesc(resp.getMessage());
                            smeTransDetailRepository.save(transactionDetail);

                            smeTransRepository.updateTransStatus(
                                    cachedSmeTrans.getId(), Constants.TransStatus.SUCCESS);
                        } else {

                            transactionDetail.setResCode(resp.getCode());
                            transactionDetail.setResDesc(resp.getMessage());
                            transactionDetail.setTranxNote("Checker confirm failed");
                            transactionDetail.setDetail("Transfer failed");
                            smeTransDetailRepository.save(transactionDetail);

//                            smeTransRepository.updateTransStatus(
//                                    cachedSmeTrans.getId(), Constants.TransStatus.APPROVE_FAIL);
                        }

                        cachedSmeTrans.setRequestProcessed(true);
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);

                        // cap nhat trang thai lo (neu co)
                        commonService.updateBatchStatus(Arrays.asList(String.valueOf(cachedSmeTrans.getId())));

                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, req.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, req.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            log.info("Error: ", e);
        } finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse initBatchCashPayment(InitConfirmTransBatchRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        int transLimit = Integer.parseInt(commonService.getConfig("LIMIT_TRANS_BATCH", "10"));
        if (rq.getTranxIds().size() > transLimit) {
            rp.setCode(Constants.ResCode.ERROR_0205);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.LIMIT_TRANS_FAIL, rq.getLang()));
            return rp;
        }
        try {
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Lấy ptxt
                        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
                        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
                            rp.setCode(checkAuthen.getCode());
                            rp.setMessage(commonService.getMessage(checkAuthen.getCode(), rq.getLang()));
                            return rp;
                        }
                        // get list account
                        List<AccountDTO> listAccount = user.getListAccount();
                        if (listAccount.isEmpty()) {
                            AccountListingBankRequest accListReq = new AccountListingBankRequest();
                            accListReq.setAccountGroupType("ALL");
                            accListReq.setCif(user.getCif());
                            accListReq.setJoinable(true);
                            AccountListingBankResponse accountListingResponse =
                                    coreQueryClient.getAccountListByCif(accListReq);

                            if (!accountListingResponse.getResponseStatus().getIsSuccess()) {
                                log.info("Failed to query account list");
                            }

                            listAccount =
                                    modelMapper.map(
                                            accountListingResponse.getListAccount(),
                                            new TypeToken<List<AccountDTO>>() {
                                            }.getType());

                            user.setListAccount(listAccount);
                            redisCacheService.putSession(rq, user);
                        }
                        log.info("List account: " + listAccount);
                        // get list trans
                        List<SmeTrans> smeTranss = smeTransRepository.findAllById(rq.getTranxIds().stream().map(Long::parseLong).collect(Collectors.toList()));

                        if (smeTranss.parallelStream()
                                .filter(t -> !t.getCifNo().equals(user.getCif())).findFirst()
                                .isPresent()) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_96,
                                    commonService.getMessage(Constants.MessageCode.INVALID_DATA, rq.getLang()));
                        }

                        // get list from acc
                        List<String> fromAccs = smeTranss.stream()
                                .filter(p -> !p.getTranxType().equals(Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE)
                                        && !p.getTranxType().equals(Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED))
                                .map(p -> p.getFromAcc()).distinct().collect(Collectors.toList());

                        BigDecimal totalAmount = BigDecimal.ZERO;
                        BigDecimal totalUsd = BigDecimal.ZERO;
                        BigDecimal totalVnd = BigDecimal.ZERO;
                        for (String fromacc : fromAccs) {
                            // Kiểm tra tài khoản debit
                            Optional<AccountDTO> opt =
                                    listAccount.stream()
                                            .filter(
                                                    x ->
                                                            (fromacc.equalsIgnoreCase(x.getAccountNo())
                                                                    || fromacc
                                                                    .equalsIgnoreCase(x.getAccountAlias())))
                                            .findFirst();

                            if (!opt.isPresent()) {
                                rp.setCode(Constants.ResCode.INFO_23);
                                rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                                return rp;
                            }
                            AccountDTO acc = opt.get();
                            // Lấy thông tin chi tiết tài khoản debit
                            log.info("Get Debit account details for {}", fromacc);
                            AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                            accDetailsReq.setAccountNo(fromacc);
                            accDetailsReq.setAccountType(acc.getAccountType());
                            accDetailsReq.setAlias(fromacc.equalsIgnoreCase(acc.getAccountAlias()));
                            AccountDetailBankResponse debitBankResp =
                                    coreQueryClient.getDDAccountDetails(accDetailsReq);
                            if (debitBankResp.getResponseStatus().getIsFail()
                                    || (!"00".equals(debitBankResp.getResponseStatus().getResCode())
                                    && !"0".equals(debitBankResp.getResponseStatus().getResCode()))) {
                                log.info("Failed to get debit account details");
                                rp.setCode(debitBankResp.getResponseStatus().getResCode());
                                rp.setMessage(debitBankResp.getResponseStatus().getResMessage());
                                return rp;
                            }
                            // Kiểm tra trạng thái tài khoản debit
                            if (!Constants.ALLOWED_ACC_STT_DEBIT.contains(debitBankResp.getAccountStatus())) {
                                log.info("Invalid debit account status: {}", debitBankResp.getAccountStatus());
                                rp.setCode(Constants.ResCode.INFO_37);
                                rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_37, rq.getLang()));
                                return rp;
                            }
                            // Kiểm tra trạng thái tài khoản debit
                            rp =
                                    transactionService.checkDebitAccountStatus(
                                            rp,
                                            fromacc,
                                            acc.getAccountType(),
                                            fromacc.equalsIgnoreCase(acc.getAccountAlias()),
                                            true,
                                            rq.getLang());
                            if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                                return rp;
                            }

                            // tính tổng số tiền giao dich
                            BigDecimal totalDebitAmount = BigDecimal.ZERO;
                            List<SmeTrans> smeTransList = smeTranss.stream().filter(p -> p.getFromAcc().equals(fromacc)).collect(Collectors.toList());
                            for (SmeTrans s : smeTransList) {
                                TransactionMetaDataDTO transactionMetaDataDTO = gson.fromJson(s.getMetadata(), TransactionMetaDataDTO.class);
                                totalDebitAmount = totalDebitAmount.add(BigDecimal.valueOf(transactionMetaDataDTO.getFutureTransData().getDebitAccount().getOriginAmount()));
                                if ("VND".equals(transactionMetaDataDTO.getCheckLimitTrans().getCcy())) {
                                    totalVnd = totalVnd.add(BigDecimal.valueOf(transactionMetaDataDTO.getCheckLimitTrans().getAmount()));
                                } else {
                                    totalUsd = totalUsd.add(BigDecimal.valueOf(transactionMetaDataDTO.getCheckLimitTrans().getAmount()));
                                }
                            }
                            totalAmount = totalAmount.add(totalDebitAmount);

                            // Kiểm tra số dư tài khoản debit
                            String minBal =
                                    "VND".equals(debitBankResp.getCurCode())
                                            ? commonService.getConfig("MIN_BALANCE", "50000")
                                            : commonService.getConfig("MIN_BALANCE_FOR", "10");
                            log.info("minBal: {}", minBal);
                            if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                    .subtract(new BigDecimal(minBal))
                                    .doubleValue()
                                    < totalDebitAmount.doubleValue()) {
                                rp.setCode(Constants.ResCode.ERROR_112);
                                rp.setMessage(
                                        commonService.getMessage(Constants.MessageCode.ERROR_112, rq.getLang()));
                                return rp;
                            }
                        }

                        // check trans info
                        for (SmeTrans smeTrans : smeTranss) {
                            if (smeTrans == null
                                    || (!"5".equals(smeTrans.getStatus()) && !"10".equals(smeTrans.getStatus()))) {
                                return new BaseClientResponse(
                                        Constants.ResCode.ERROR_0204,
                                        commonService.getMessage(Constants.MessageCode.CONFIRM_TRANS_FAIL, rq.getLang()));
                            }

                            // Check role type được quyền thao tác chức năng hay không
                            rp =
                                    transactionService.validateUserAndServiceCode(
                                            rp,
                                            user,
                                            smeTrans.getTranxType(),
                                            "2",
                                            rq.getLang(),
                                            smeTrans.getCreatedUser());
                            if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                                return rp;
                            }
                        }

                        // check han muc
                        List<String> serviceCodes = smeTranss.stream().map(p -> p.getTranxType()).distinct().collect(Collectors.toList());
                        List<String> ccys = new ArrayList<>();
                        ccys.add("USD");
                        ccys.add("VND");
                        for (String s : serviceCodes) {
                            for (String c : ccys) {
                                List<SmeTrans> trans = smeTranss.stream().filter(p ->
                                        s.equals(p.getTranxType())).collect(Collectors.toList());
                                if (!trans.isEmpty()) {
                                    double amount = 0;
                                    for (SmeTrans st : trans) {
                                        TransactionMetaDataDTO transactionMetaDataDTO = gson.fromJson(st.getMetadata(), TransactionMetaDataDTO.class);
                                        if (c.equals(transactionMetaDataDTO.getCheckLimitTrans().getCcy())) {
                                            amount += transactionMetaDataDTO.getCheckLimitTrans().getAmount();
                                        }
                                    }
                                    log.info("service code {} ccy {} amount {}", s, c, amount);
                                    BaseClientResponse checkLimit = null;
                                    if ("VND".equals(c) && amount > 0 && totalVnd.doubleValue() > 0) {
                                        // Kiểm tra hạn mức giao dịch chung
                                        checkLimit =
                                                transactionLimitService.checkTranLimitBatch(
                                                        user,
                                                        rq,
                                                        s,
                                                        amount,
                                                        c,
                                                        checkAuthen.getAuthenMethod(),
                                                        true, totalVnd.doubleValue());
                                    } else {
                                        if (amount > 0 && totalUsd.doubleValue() > 0) {
                                            // Kiểm tra hạn mức giao dịch chung
                                            checkLimit =
                                                    transactionLimitService.checkTranLimitBatch(
                                                            user,
                                                            rq,
                                                            s,
                                                            amount,
                                                            c,
                                                            checkAuthen.getAuthenMethod(),
                                                            true, totalUsd.doubleValue());
                                        }
                                    }

                                    if (checkLimit != null && !Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                                        log.error("Lỗi hạn mức");
                                        return checkLimit;
                                    }
                                }
                            }
                        }

                        // Khởi tạo ptxt
                        String tranToken = transactionService.genTranToken();
                        TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTranss.get(0).getMetadata(), TransactionMetaDataDTO.class);
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTranss.get(0).getFromAcc(),
                                        metaDataDTO.getRecipient().getId(),
                                        smeTranss.get(0).getId(),
                                        String.valueOf(smeTranss.get(0).getAmount()),
                                        tranToken,
                                        smeTranss.get(0).getTranxType(),
                                        "",
                                        smeTranss.get(0).getCcy());
                        if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                            log.error("init authen error: " + intAuthen.getCode());
                            rp.setCode(intAuthen.getCode());
                            rp.setMessage(intAuthen.getMessage());
                            return rp;
                        }

                        TransBatchDTO smeTrans = TransBatchDTO.builder()
                                .tranxType(smeTranss.get(0).getTranxType())
                                .ccy(smeTranss.get(0).getCcy())
                                .id(smeTranss.get(0).getId())
                                .lang(rq.getLang())
                                .source(rq.getSource())
                                .approvedUser(user.getUsername())
                                .authenType(checkAuthen.getAuthenMethod())
                                .listTransId(rq.getTranxIds())
                                .amount(smeTranss.get(0).getAmount()).build();
                        smeTrans.setAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans.setChallenge(intAuthen.getDataAuthen());
                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        // cập nhật reason
                        smeTransRepository.updateTransStatusAndReasonAndApproveUserBatch(rq.getTranxIds().stream().map(Long::parseLong).collect(Collectors.toList()),
                                Constants.TransStatus.CHEKER_WAIT_CONFIRM, rq.getReason(), rq.getUser());

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranToken(tranToken);
                        dataRp.setDataAuthen(intAuthen.getDataAuthen());
                        dataRp.setTranxId(String.valueOf(smeTrans.getId()));
                        dataRp.setServiceCode(smeTranss.get(0).getTranxType());
                        dataRp.setAmount(smeTranss.get(0).getAmount());
                        dataRp.setFromAccount(smeTranss.get(0).getFromAcc());
                        dataRp.setToAccount(smeTranss.get(0).getToAcc());
                        dataRp.setCurCode(smeTranss.get(0).getCcy());

                        String authenType = checkAuthen.getAuthenMethod();
                        dataRp.setAuthenType(authenType);
                        log.info("Checker authen type: {}", authenType);

                        rp.setData(dataRp);

                        return rp;
                    default:
                        log.info("Invalid user status");
                        rp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        rp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                rp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                rp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return rp;
    }

    public BaseClientResponse confirmBatchCashPayment(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        boolean isDeleteCache = true;
        try {
            // Kiểm tra giao dịch có đang xử lý
            boolean isExe = !redisCacheService.setnx(req.getTranxId(), 15l);
            if (isExe) {
                isDeleteCache = false;
                String resCode = "028";
                return new BaseClientResponse(
                        resCode,
                        commonService.getMessage("DUPL-028", req.getLang()));
            }
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(req);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Valid giao dich
                        BaseClientResponse valid = transactionService.validTxn(req);
                        if (!Constants.ResCode.INFO_00.equals(valid.getCode())) {
                            return valid;
                        }

                        // Get cache value
                        TransBatchDTO cachedSmeTrans = gson.fromJson(redisCacheService.getTxn(req), TransBatchDTO.class);
                        if ("1".equals(cachedSmeTrans.getStatus())) {
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }
                        // Xác thực OTP
                        InitAuthenResponse initAuthenResponse =
                                transactionService.confirmAuthenTxn(
                                        user,
                                        req,
                                        cachedSmeTrans.getAuthenType(),
                                        cachedSmeTrans.getId(),
                                        req.getAuthenValue(),
                                        req.getTranToken(),
                                        Strings.nullToEmpty(cachedSmeTrans.getChallenge()),
                                        String.valueOf(cachedSmeTrans.getAmount()),
                                        cachedSmeTrans.getTranxType(),
                                        cachedSmeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(initAuthenResponse.getCode())) {
                            resp.setCode(initAuthenResponse.getCode());
                            resp.setMessage(initAuthenResponse.getMessage());
                            return resp;
                        }

                        TransConfirmRes transConfirmRes =
                                TransConfirmRes.builder()
                                        .tranxId(String.valueOf(cachedSmeTrans.getId()))
                                        .amount(cachedSmeTrans.getAmount())
                                        .tranxDate(CommonUtils.getDate("dd/MM/yyyy HH:mm:ss"))
                                        .build();

                        resp.setData(transConfirmRes);

                        // push trans to queue
                        firstRabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, cachedSmeTrans);
                        // update trans status
                        smeTransRepository.updateTransStatusBatch(
                                cachedSmeTrans.getListTransId().stream().map(Long::parseLong).collect(Collectors.toList()), Constants.TransStatus.WAIT_CONFIRM, user.getUsername());
                        cachedSmeTrans.setStatus("1");
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);

                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, req.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, req.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            log.info("Error: ", e);
        } finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }
}
