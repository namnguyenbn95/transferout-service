package vn.vnpay.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.AccountDetailBankRequest;
import vn.vnpay.commoninterface.bank.request.ExchangeRateInquiryBankRequest;
import vn.vnpay.commoninterface.bank.response.AccountDetailBankResponse;
import vn.vnpay.commoninterface.bank.response.ExchangeRateInquiryBankResponse;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.feignclient.DigiCoreTransClient;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.AuthenMethodResponse;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.commoninterface.response.InitAuthenResponse;
import vn.vnpay.commoninterface.service.*;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.entity.*;
import vn.vnpay.dbinterface.repository.CmCurrencyRepository;
import vn.vnpay.dbinterface.repository.MbServiceRepository;
import vn.vnpay.dbinterface.repository.SmeTransDetailRepository;
import vn.vnpay.dbinterface.repository.SmeTransRepository;
import vn.vnpay.request.IBPSViaCardRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IBPSViaCardService {

    @Autowired
    CommonService commonService;

    @Autowired
    RedisCacheService cacheService;

    @Autowired
    Gson gson;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionFeeService transactionFeeService;

    @Autowired
    DigiCoreTransClient coreTransClient;

    @Autowired
    CoreQueryClient coreQueryClient;

    @Autowired
    MbServiceRepository mbServiceRepository;

    @Autowired
    SmeTransRepository smeTransRepository;

    @Autowired
    SmeTransDetailRepository smeTransDetailRepository;

    @Autowired
    TransactionLimitService transactionLimitService;

    @Autowired
    private CmCurrencyRepository cmCurrencyRepository;

    @Autowired
    private CaptchaService captchaService;

    public BaseClientResponse ibpsViaCardMakerInit(IBPSViaCardRequest req, SmeCustomerUser user) {
        BaseClientResponse baseResp = new BaseClientResponse(Constants.ResCode.INFO_00, commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        BaseTransactionResponse dataRp = new BaseTransactionResponse();
        TransactionMetaDataDTO metadata = TransactionMetaDataDTO.builder().build();
        StringBuilder remarkBuilder = new StringBuilder();

        // get list currency
        List<CmCurrency> cmCurrencies = cmCurrencyRepository.findByStatus("1");
        log.info("cmCurrencies {}", gson.toJson(cmCurrencies));

        // Kiểm tra tài khoản debit
        List<AccountDTO> listAccount = user.getListAccount();
        Optional<AccountDTO> opt = listAccount.stream().filter(x -> req.getFromAcc().equalsIgnoreCase(x.getAccountNo()) || req.getFromAcc().equalsIgnoreCase(x.getAccountAlias())).findFirst();

        if (!opt.isPresent()) {
            baseResp.setCode(Constants.ResCode.INFO_23);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, req.getLang()));
            return baseResp;
        }
        AccountDTO acc = opt.get();

        // Init transaction remark
        if (Constants.SOURCE_IB.equals(req.getSource())) {
            remarkBuilder.append("IBBIZ$1.IBPS.");
        } else {
            remarkBuilder.append("MBBIZ$1.IBPS.");
        }
        remarkBuilder.append(req.getContent());

        // Lấy thông tin chi tiết tài khoản debit
        log.info("Get Debit account details for {}", req.getFromAcc());
        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
        accDetailsReq.setAccountNo(req.getFromAcc());
        accDetailsReq.setAccountType(acc.getAccountType());
        accDetailsReq.setAlias(req.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));
        AccountDetailBankResponse debitBankResp = coreQueryClient.getDDAccountDetails(accDetailsReq);
        if (!"0".equals(debitBankResp.getResponseStatus().getResCode())) {
            log.info("Failed to get debit account details");
            String code = "0199";
            if (debitBankResp.getResponseStatus().getIsFail()) {
                code = debitBankResp.getResponseStatus().getResCode();
            }
            baseResp.setCode(code);
            baseResp.setMessage(commonService.getMessage("ACC-DETAIL-" + code, req.getLang()));
            return baseResp;
        }
        // Kiểm tra trạng thái tài khoản debit
        if (!Constants.ALLOWED_ACC_STT_DEBIT.contains(debitBankResp.getAccountStatus())) {
            log.info("Invalid debit account status: {}", debitBankResp.getAccountStatus());
            baseResp.setCode(Constants.ResCode.INFO_37);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_37, req.getLang()));
            return baseResp;
        }

        Optional<CmCurrency> cmCurrencyOpt = cmCurrencies.parallelStream().filter(p -> p.getCurrencyCode().equals(debitBankResp.getCurCode())).findFirst();
        CmCurrency cmCurrency = null;
        if (cmCurrencyOpt.isPresent()) {
            cmCurrency = cmCurrencyOpt.get();
            log.info("cmCurrency {}", gson.toJson(cmCurrency));
        }

        metadata.setDebitName(debitBankResp.getAccountName());
        metadata.setDebitAddr(debitBankResp.getAccountAddress());
        metadata.setTranViaCard(true);
        metadata.setCardMaskingNumber(req.getCardMaskingNumber());

        String serviceCode = Constants.ServiceCode.TRANS_OUT_VIA_ACCNO;

        // Lấy thông tin service type
        Optional<MbService> mbServiceOpt = mbServiceRepository.findByServiceCode(serviceCode);
        if (!mbServiceOpt.isPresent()) {
            log.info("Invalid service code: {}", serviceCode);
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            return baseResp;
        }

        // Kiểm tra quyền giao dịch tài khoản nguồn
        baseResp = transactionService.validateTransAuthorityForAccount(baseResp, user, req.getFromAcc(), serviceCode, req.getLang());
        if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
            return baseResp;
        }

        // Kiếm tra role type được quyền thực hiện chức năng hay không
        baseResp = transactionService.validateUserAndServiceCode(baseResp, user, serviceCode, "1", req.getLang(), null);
        if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
            return baseResp;
        }

        // Kiểm tra mã sản phẩm tài khoản
        baseResp = transactionService.validateAccountProduct(
                baseResp,
                serviceCode,
                debitBankResp.getProductCode(),
                null,
                req.getLang());
        if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
            return baseResp;
        }

        // Kiểm tra trạng thái tài khoản debit
        baseResp = transactionService.checkDebitAccountStatus(
                baseResp,
                req.getFromAcc(),
                acc.getAccountType(),
                req.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()),
                true,
                req.getLang());
        if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
            return baseResp;
        }

        // Lấy phương thức xác thực
        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
            baseResp.setCode(checkAuthen.getCode());
            baseResp.setMessage(commonService.getMessage(checkAuthen.getCode(), req.getLang()));
            return baseResp;
        }

        String authenType = checkAuthen.getAuthenMethod();
        dataRp.setAuthenType(authenType);
        log.info("Maker authen type: {}", authenType);

        // Lấy tỉ giá ngoại tệ
        BigDecimal rate = BigDecimal.ONE; // VND
        BigDecimal rateUSD = BigDecimal.ONE;
        ExchangeRateInquiryBankResponse tiGiaNgoaiTe = null;
        ExchangeRateInquiryBankResponse tiGiaUsd = null;
        if (!"VND".equalsIgnoreCase(debitBankResp.getCurCode())) {
            ExchangeRateInquiryBankRequest bankReq = ExchangeRateInquiryBankRequest.builder()
                    .currency(debitBankResp.getCurCode())
                    .build();
            ExchangeRateInquiryBankResponse bankResp = coreQueryClient.getExchangeRateInquiry(bankReq);
            if (!bankResp.getResponseStatus().getIsSuccess()) {
                log.info("Failed to get exchange rate");
                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(bankResp.getResponseStatus().getResMessage());
                return baseResp;
            }
            tiGiaNgoaiTe = bankResp;
            rate = bankResp.getAppXferBuy();
            if ("USD".equals(debitBankResp.getCurCode())) {
                rateUSD = rate;
            } else {
                bankResp = coreQueryClient.getExchangeRateInquiry(ExchangeRateInquiryBankRequest.builder().currency("USD").build());
                if (bankResp.getResponseStatus().getIsFail()) {
                    baseResp.setCode(bankResp.getResponseStatus().getResCode());
                    baseResp.setMessage(commonService.getMessage(bankResp.getResponseStatus().getResCode(), req.getLang()));
                    return baseResp;
                }
                //rateUSD = bankResp.getAppXferBuy();
                rateUSD = bankResp.getSellRate();
                tiGiaUsd = bankResp;
            }
        }

        // Quy đổi ngoại tệ <-> VND
        BigDecimal amountVND, originAmount;
        if ("VND".equalsIgnoreCase(req.getCurCode())) {
            amountVND = new BigDecimal(req.getAmount().replace(",", ""));
            originAmount = amountVND.divide(rate, 2, RoundingMode.HALF_UP);
        } else {
            originAmount = new BigDecimal(req.getAmount().replace(",", ""));
            amountVND = originAmount.multiply(rate);
        }

        // Tính phí giao dịch
        String vatExamptFlag = commonService.getVatExemptFlag(req);
        GetFeeTransferDTO input = GetFeeTransferDTO.builder()
                .accountPkgCode(user.getAccountPkgCode())
                .pkgCode(user.getPackageCode())
                .promCode(user.getValidPromCode())
                .serviceCode(serviceCode)
                .authMethod(authenType)
                .ccy(acc.getCurCode())
                .amount(originAmount)
                .exchangeAmount(amountVND)
                .isExamptVat("Y".equalsIgnoreCase(vatExamptFlag))
                .creditAccount(req.getToAcc())
                .tiGiaNgoaiTe(tiGiaNgoaiTe)
                .tiGiaUsd(tiGiaUsd)
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
            feeAmt = feeU.multiply(rateUSD).setScale(0, RoundingMode.HALF_UP);
            feeVat = vatU.multiply(rateUSD).setScale(0, RoundingMode.HALF_UP);
            originFeeAmt = feeAmt.divide(rate, 2, RoundingMode.HALF_UP);
            originFeeVat = feeVat.divide(rate, 2, RoundingMode.HALF_UP);
        }
        BigDecimal debitAmount, creditAmount, originDebitAmt;
        if ("1".equals(req.getFeeType())) {
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
        metadata.setBeneBranchCode(req.getBeneBranchCode());
        metadata.setBeneBranchName(req.getBeneBranchName());
        metadata.setBeneBankCode(req.getBeneBankCode());
        metadata.setBeneBankName(req.getBeneBankName());
        metadata.setBeneCityCode(req.getBeneCityCode());
        metadata.setBeneCityName(req.getBeneCityName());
        metadata.setVatExamptFlag(vatExamptFlag);
        metadata.setOriginAmount(originAmount.doubleValue());
        metadata.setFeeU(feeU);
        metadata.setVatU(vatU);
        metadata.setClientRqCcy(req.getCurCode());

        // Kiểm tra giao dịch đi thẳng
        boolean isExecTrans = transactionService.isExecTrans(user.getRoleType(), user.getUsername(), serviceCode, req.getFromAcc());
        log.info("isExecTrans? {}", isExecTrans);

        // Kiểm tra số dư tài khoản debit
        String minBal = "VND".equals(debitBankResp.getCurCode())
                ? commonService.getConfig("MIN_BALANCE", "50000")
                : commonService.getConfig("MIN_BALANCE_FOR", "10");
        log.info("minBal: {}", minBal);
        if ((new BigDecimal(debitBankResp.getAvaiableAmount())).subtract(new BigDecimal(minBal)).doubleValue() < originDebitAmt.doubleValue()) {
            if (isExecTrans) {
                baseResp.setCode(Constants.ResCode.ERROR_112);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_112, req.getLang()));
                return baseResp;
            } else {
                if (!"1".equals(req.getIsByPassNotBalance())) {
                    baseResp.setCode(Constants.ResCode.INFO_45);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_45, req.getLang()));
                    return baseResp;
                }
            }
        }

        if (creditAmount.longValue() < 0) {
            log.info("creditAmount < 0");
            baseResp.setCode(Constants.ResCode.ERROR_113);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_113, req.getLang()));
            return baseResp;
        }

        // Convert tiền tệ khác USD, và VND để tính toàn hạn mức
        CheckLimitTrans checkLimitTrans = transactionLimitService.convertCcyAmount(baseResp, acc.getCurCode(), originAmount, amountVND);
        if (!Constants.ResCode.INFO_00.equals(baseResp.getCode())) {
            return baseResp;
        }
        // Kiểm tra hạn mức giao dịch chung
        metadata.setCheckLimitTrans(checkLimitTrans);
        metadata.setExecTrans(isExecTrans);
        BaseClientResponse checkLimit = transactionLimitService.checkTranLimit(
                user,
                req,
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
        smeTrans.setTranxContent(req.getContent());
        smeTrans.setMakerAuthenType(authenType);
        smeTrans.setCusName(user.getCusName());
        smeTrans.setCreatedUser(user.getUsername());
        smeTrans.setCreatedMobile(user.getMobileOtp());
        smeTrans.setFromAcc(req.getFromAcc());
        smeTrans.setToAcc(req.getToAcc());
        smeTrans.setTranxType(serviceCode);
        smeTrans.setTranxNote("Maker init");
        smeTrans.setCifNo(user.getCif());
        smeTrans.setTranxTime(LocalDateTime.now());
        smeTrans.setServiceType(mbServiceOpt.get().getServiceType());
        smeTrans.setCcy(acc.getCurCode());
        smeTrans.setStatus(Constants.TransStatus.MAKER_WAIT_CONFIRM);
        smeTrans.setFeeType(req.getFeeType());
        smeTrans.setCreditName(req.getToAccName());
        smeTrans.setBeneBankCode(req.getBeneBankCode());
        smeTrans.setBranchCode(user.getBranchCode());
        smeTrans.setChannel(req.getSource());
        smeTrans.setFeeOnAmt(feeVat.doubleValue());
        smeTrans.setFlatFee(feeAmt.doubleValue());
        smeTrans.setAmount(originAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());
        smeTrans.setTotalAmount(amountVND.longValue());
        smeTrans.setDebitBranchCode(debitBankResp.getBranchNo());
        smeTrans.setCifInt(user.getCifInt());
        smeTrans.setRealAmount(debitAmount);

        // Tài khoản nguồn
        DebitAccountDTO debitAccount = DebitAccountDTO.builder()
                .cif(user.getCifInt())
                .accountNo(acc.getAccountNo())
                .accountAlias(acc.getAccountAlias())
                .accountType(acc.getAccountType())
                .accountHolderName(debitBankResp.getAccountName())
                .amountVND(debitAmount.longValue())
                .originAmount(originDebitAmt.doubleValue())
                .currency(acc.getCurCode())
                .branch(debitBankResp.getBranchNo())
                .rate(String.valueOf(rate))
                .build();

        String creditBankCode =
                Strings.isNullOrEmpty(req.getBeneBranchCode())
                        ? req.getBeneBankCode()
                        : req.getBeneBranchCode();

        // Tài khoản đích
        CreditAccountDTO creditAccount = CreditAccountDTO.builder()
                .accountNo(req.getToAcc())
                .accountHolderName(req.getToAccName())
                .bankCode(creditBankCode)
                .branch(creditBankCode)
                .bankName(Strings.nullToEmpty(req.getBeneBankName()))
                .amountVND(creditAmount.longValue())
                .originAmount(creditAmount.longValue())
                .currency("VND")
                .accountType("D")
                .rate("1")
                .build();

        FeeDTO fee = FeeDTO.builder()
                .amount(feeAmt.doubleValue())
                .amountVND(feeAmt.doubleValue())
                .authMethod(0)
                .currency(acc.getCurCode())
                .originAmount(originFeeAmt.doubleValue())
                .originAuthMethod(0.0)
                .originVatAmount(originFeeVat.doubleValue())
                .type(Integer.parseInt(req.getFeeType()))
                .vatAmount(feeVat.doubleValue())
                .vatAmountVND(feeVat.doubleValue())
                .build(); // Phí

        // kiem tra loai tien te co cho phep so le thap phan
        if (cmCurrency != null && !"vnd".equalsIgnoreCase(debitAccount.getCurrency()) &&
                "0".equals(cmCurrency.getIsDecimal())) {
            // kiem tra neu amount request client truyen len la so thap phan
            if (!captchaService.isNotDecimalDigits(Double.valueOf(req.getAmount()))) {
                log.error("amount is decimal value");
                baseResp.setCode(Constants.ResCode.ERROR_96);
                baseResp.setMessage("amount is decimal value");

                return baseResp;
            }
            log.info("rounding decimal amount");
            BigDecimal originAmountDeb = BigDecimal.valueOf(debitAccount.getOriginAmount());
            BigDecimal amountVnd = BigDecimal.valueOf(debitAccount.getAmountVND());
            debitAccount.setOriginAmount(originAmountDeb.setScale(0, RoundingMode.HALF_UP).doubleValue());
            debitAccount.setAmountVND(amountVnd.setScale(0, RoundingMode.HALF_UP).doubleValue());

            BigDecimal originAmountCre = BigDecimal.valueOf(creditAccount.getOriginAmount());
            BigDecimal amountVndCre = BigDecimal.valueOf(creditAccount.getAmountVND());
            creditAccount.setOriginAmount(originAmountCre.setScale(0, RoundingMode.HALF_UP).doubleValue());
            creditAccount.setAmountVND(amountVndCre.setScale(0, RoundingMode.HALF_UP).doubleValue());

            BigDecimal originAmountFee = BigDecimal.valueOf(fee.getOriginAmount());
            BigDecimal originVatAmountFee = BigDecimal.valueOf(fee.getOriginVatAmount());
            BigDecimal amountFee = BigDecimal.valueOf(fee.getAmount());
            BigDecimal amountVndFee = BigDecimal.valueOf(fee.getAmountVND());
            BigDecimal vatAmountFee = BigDecimal.valueOf(fee.getVatAmount());
            BigDecimal vatAmountVndFee = BigDecimal.valueOf(fee.getVatAmountVND());
            fee.setOriginAmount(originAmountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
            fee.setOriginVatAmount(originVatAmountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
            fee.setAmount(amountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
            fee.setAmountVND(amountVndFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
            fee.setVatAmount(vatAmountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
            fee.setVatAmountVND(vatAmountVndFee.setScale(0, RoundingMode.HALF_UP).doubleValue());

            originAmount = originAmount.setScale(0, RoundingMode.HALF_UP);
            metadata.setOriginAmount(originAmount.doubleValue());
            double notBal = Math.abs(originAmount.doubleValue() + fee.getOriginAmount() + fee.getOriginVatAmount() - debitAccount.getOriginAmount());
            if (originAmount.doubleValue() + fee.getOriginAmount() + fee.getOriginVatAmount() > debitAccount.getOriginAmount()) {
                debitAccount.setOriginAmount(debitAccount.getOriginAmount() + notBal);
            } else {
                debitAccount.setOriginAmount(debitAccount.getOriginAmount() - notBal);
//                originAmount = BigDecimal.valueOf(originAmount.doubleValue() + notBal);
//                metadata.setOriginAmount(originAmount.doubleValue());
            }
        }

        metadata.setDebitAccount(debitAccount);
        metadata.setCreditAccount(creditAccount);
        metadata.setFee(fee);

        smeTrans.setMetadata(gson.toJson(metadata));
        String remarkStr = remarkBuilder.toString();
        remarkStr = remarkStr.replace("$1", String.valueOf(transId));
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
        transactionDetail.setSource(req.getSource());

        // Khởi tạo phương thức xác thực
        String tranToken = transactionService.genTranToken();
        try {
            InitAuthenResponse intAuthen = transactionService.intAuthen(
                    user,
                    req,
                    authenType,
                    req.getFromAcc(),
                    req.getToAcc(),
                    smeTrans.getId(),
                    req.getAmount(),
                    tranToken,
                    serviceCode,
                    "",
                    req.getCurCode());
            if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                log.error("init authen error: " + intAuthen.getCode());

                transactionDetail.setResCode(intAuthen.getCode());
                transactionDetail.setResDesc(intAuthen.getDataAuthen());
                transactionDetail.setTranxNote("Maker init failed");
                transactionDetail.setDetail("Init authen method failed");
                smeTransDetailRepository.save(transactionDetail);

                baseResp.setCode(intAuthen.getCode());
                baseResp.setMessage(intAuthen.getMessage());
                return baseResp;
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

            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            return baseResp;
        }

        smeTransDetailRepository.save(transactionDetail);

        // Lưu cache giao dich
        cacheService.pushTxn(req, tranToken, smeTrans);

        // Trả ra dữ liệu cho client
        dataRp.setFee(fee.getOriginAmount());
        dataRp.setVat(fee.getOriginVatAmount());
        dataRp.setTotalFee(fee.getOriginAmount() + fee.getOriginVatAmount());
        dataRp.setClientRqCcy(req.getCurCode());

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
        dataRp.setToAccName(smeTrans.getCreditName());
        dataRp.setServiceCode(smeTrans.getTranxType());
        dataRp.setFeeToShow(CommonUtils.formatAmount(dataRp.getFee(), fee.getCurrency()));
        dataRp.setVatToShow(CommonUtils.formatAmount(dataRp.getVat(), fee.getCurrency()));
        dataRp.setTotalFeeToShow(CommonUtils.formatAmount(dataRp.getTotalFee(), fee.getCurrency()));
        dataRp.setTranxId(String.valueOf(smeTrans.getId()));

        baseResp.setData(dataRp);
        return baseResp;
    }

    public BaseClientResponse ibpsViaCardMakerConfirm(BaseConfirmRq req, SmeCustomerUser user) throws Exception {
        BaseClientResponse baseResp = new BaseClientResponse(Constants.ResCode.INFO_00, commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        BaseTransactionResponse dataRp = new BaseTransactionResponse();
        boolean isDeleteCache = true;
        try{
            boolean isExe = !cacheService.setnx(req.getTranxId(), 15l);
            if (isExe) {
                log.error("trans dup");
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
            SmeTrans cachedSmeTrans = gson.fromJson(cacheService.getTxn(req), SmeTrans.class);

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
                baseResp.setCode(Constants.ResCode.ERROR_96);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return baseResp;
            }

            if (!user.getUsername().equals(cachedSmeTrans.getCreatedUser())) {
                log.info("Created user ({}) and request user ({}) are not the same", cachedSmeTrans.getCreatedUser(), user.getUsername());
                baseResp.setCode(Constants.ResCode.ERROR_96);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return baseResp;
            }

            if (!cachedSmeTrans.getMakerAuthenType().equals(req.getAuthenType())) {
                log.info("Invalid authenType: {}", req.getAuthenType());
                baseResp.setCode(Constants.ResCode.ERROR_96);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INVALID_DATA, req.getLang()));
                return baseResp;
            }

            // Kiếm tra role type được quyền thực hiện chức năng hay không
            baseResp =
                    transactionService.validateUserAndServiceCode(
                            baseResp, user, cachedSmeTrans.getTranxType(), "1", req.getLang(), null);
            if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
                return baseResp;
            }

            // Cap nhat chi tiet giao dich
            SmeTransactionDetail transactionDetail = new SmeTransactionDetail();
            transactionDetail.setTranxId(cachedSmeTrans.getId());
            transactionDetail.setTranxPhase(Constants.TransPhase.MAKER_CONFIRM);
            transactionDetail.setResCode(Constants.ResCode.INFO_00);
            transactionDetail.setResDesc("Success");
            transactionDetail.setTranxNote("Maker confirm");
            transactionDetail.setDetail("Maker confirm success");
            transactionDetail.setCreatedDate(LocalDateTime.now());
            transactionDetail.setSource(req.getSource());

            try {
                // Xác thực OTP
                InitAuthenResponse initAuthenResponse =
                        transactionService.confirmAuthenTxn(
                                user,
                                req,
                                req.getAuthenType(),
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
                    transactionDetail.setTranxNote("Maker confirm failed");
                    transactionDetail.setDetail("Authenticate failed");
                    smeTransDetailRepository.save(transactionDetail);

                    baseResp.setCode(initAuthenResponse.getCode());
                    baseResp.setMessage(initAuthenResponse.getMessage());
                    return baseResp;
                }
            } catch (Exception e) {
                log.info("Error: ", e);
                transactionDetail.setResCode(Constants.ResCode.ERROR_96);
                transactionDetail.setResDesc("Error");
                transactionDetail.setTranxNote("Maker confirm failed");
                transactionDetail.setDetail("Authenticate failed");

                baseResp.setCode(Constants.ResCode.ERROR_96);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return baseResp;
            }
            smeTransDetailRepository.save(transactionDetail);

            // Kiểm tra giao dịch đi ngay hay chờ duyệt
            TransactionMetaDataDTO metadata = gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);

            dataRp.setIsExecTrans(metadata.isExecTrans() ? "1" : "0");
            log.info("=====: " + cachedSmeTrans.getBranchCode());
            // Xử lý lưu giao dich để check hạn mức
            String type;
            if (metadata.isExecTrans()) {
                baseResp = transactionService.execTransOutIBPS(baseResp, cachedSmeTrans, req);
                if (!baseResp.getCode().equals(Constants.ResCode.INFO_00)) {
                    transactionDetail.setResCode(baseResp.getCode());
                    transactionDetail.setTranxNote(baseResp.getMessage());
                    transactionDetail.setDetail(baseResp.getMessage());
                    smeTransDetailRepository.save(transactionDetail);
                    return baseResp;
                }
                smeTransDetailRepository.save(transactionDetail);
                type = "2";
            } else {
                cachedSmeTrans.setStatus(Constants.TransStatus.MAKER_SUCCESS);
                cachedSmeTrans.setTranxNote("Lập lệnh thành công");
                smeTransRepository.save(cachedSmeTrans);
                type = "1";
            }

            transactionLimitService.saveCheckTransLimit(
                    user,
                    req,
                    cachedSmeTrans.getTranxType(),
                    metadata.getCheckLimitTrans().getAmount(),
                    metadata.getCheckLimitTrans().getCcy(),
                    req.getAuthenType(),
                    type);

            smeTransRepository.save(cachedSmeTrans);
            cachedSmeTrans.setRequestProcessed(true);
            cacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);

            // Lưu chức năng gần đây
            commonService.saveFuncRecent(cachedSmeTrans.getTranxType(), user.getUsername(), user.getRoleType(), user.getConfirmType(), req.getSource());

            // Trả thông tin cho client
            dataRp.setContact(commonService.isSavedBene(
                    cachedSmeTrans.getTranxType(),
                    cachedSmeTrans.getBeneBankCode(),
                    user.getUsername(),
                    null,
                    cachedSmeTrans.getToAcc(),
                    null,
                    null));
            dataRp.setTranxId(String.valueOf(cachedSmeTrans.getId()));
            dataRp.setTranDate(CommonUtils.formatDate(new Date()));
            dataRp.setFee(metadata.getFee().getOriginAmount());
            dataRp.setExchangeFee(new BigDecimal(metadata.getFee().getAmount()));
            dataRp.setVat(metadata.getFee().getOriginVatAmount());
            dataRp.setExchangeVat(new BigDecimal(metadata.getFee().getVatAmount()));
            dataRp.setTotalFee(metadata.getFee().getOriginAmount() + metadata.getFee().getOriginVatAmount());
            dataRp.setExchangeTotalFee(new BigDecimal(metadata.getFee().getAmount() + metadata.getFee().getVatAmount()));
            dataRp.setTotalAmount(metadata.getDebitAccount().getOriginAmount());
            dataRp.setExchangeTotalAmount(new BigDecimal(metadata.getDebitAccount().getAmountVND()).longValue());
            dataRp.setAmount(metadata.getOriginAmount());
            dataRp.setExchangeAmount(new BigDecimal(cachedSmeTrans.getTotalAmount()).longValue());
            dataRp.setClientRqCcy(metadata.getClientRqCcy());
            baseResp.setData(dataRp);
        }
        catch (Exception e) {
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            log.info("Error: ", e);
        }
        finally {
            if (isDeleteCache)
                cacheService.delete(req.getTranxId());
        }
        return baseResp;
    }
}
