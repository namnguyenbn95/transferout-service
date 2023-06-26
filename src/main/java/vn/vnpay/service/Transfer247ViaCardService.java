package vn.vnpay.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.common.RestClient;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.feignclient.Tranfer247Client;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.AuthenMethodResponse;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.commoninterface.response.InitAuthenResponse;
import vn.vnpay.commoninterface.service.*;
import vn.vnpay.dbinterface.dto.DebitCardDTO;
import vn.vnpay.dbinterface.entity.CmCurrency;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.dbinterface.entity.SmeTrans;
import vn.vnpay.dbinterface.entity.SmeTransactionDetail;
import vn.vnpay.dbinterface.repository.CmCurrencyRepository;
import vn.vnpay.dbinterface.repository.SmeTransDetailRepository;
import vn.vnpay.dbinterface.repository.SmeTransRepository;
import vn.vnpay.request.Transfer247ViaAccountRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class Transfer247ViaCardService {
    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private Gson gson;

    @Autowired
    private Environment env;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionLimitService transactionLimitService;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    private SmeTransDetailRepository smeTransDetailRepository;

    @Autowired
    private CoreQueryClient coreQueryClient;

    @Autowired
    private Tranfer247Client tranfer247Client;

    @Autowired
    private TransactionFeeService transactionFeeService;

    @Autowired
    private CmCurrencyRepository cmCurrencyRepository;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RestClient restClient;


    public BaseClientResponse makerInitViaCard(Transfer247ViaAccountRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            BaseTransactionResponse dataRp = new BaseTransactionResponse();
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            // get list currency
            List<CmCurrency> cmCurrencies = cmCurrencyRepository.findByStatus("1");
            log.info("cmCurrencies {}", gson.toJson(cmCurrencies));

            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case Constants.UserStatus.ACTIVE:
                        // Kiểm tra tài khoản debit
                        String keyCard = "CARD_" + rq.getSessionId() + "_" + rq.getUser();
                        SmeCustomerUser userCard =
                                gson.fromJson(redisCacheService.get(keyCard), SmeCustomerUser.class);
                        List<DebitCardDTO> listAccount = userCard.getListDebitCard();
                        Optional<DebitCardDTO> opt =
                                listAccount.stream()
                                        .filter(x -> rq.getFromAcc().equals(x.getCardNumber()))
                                        .findFirst();

                        if (!opt.isPresent()) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }

                        DebitCardDTO debitCard = opt.get();
                        String debitAccountNo = debitCard.getPrimaryAccount();
                        debitAccountNo = debitAccountNo.startsWith("000")
                                ? debitAccountNo.substring(3) : debitAccountNo;
                        // Lấy thông tin mã sản phẩm tài khoản debit
                        log.info("Get Debit account details for {}", debitAccountNo);
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(debitAccountNo);
                        accDetailsReq.setAccountType("D");
                        accDetailsReq.setAlias(debitAccountNo.length() == 13);
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

                        Optional<CmCurrency> cmCurrencyOpt = cmCurrencies.parallelStream().filter(p -> p.getCurrencyCode().equals(debitBankResp.getCurCode())).findFirst();
                        CmCurrency cmCurrency = null;
                        if (cmCurrencyOpt.isPresent()) {
                            cmCurrency = cmCurrencyOpt.get();
                            log.info("cmCurrency {}", gson.toJson(cmCurrency));
                        }

                        if (!vn.vnpay.commoninterface.common.Constants.ALLOWED_ACC_STT_DEBIT.contains(
                                debitBankResp.getAccountStatus())) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }

                        String serviceCode = Constants.ServiceCode.FAST_TRANS_VIA_CARDNO;

                        // Check role type được quyền thao tác chức năng hay không
                        rp =
                                transactionService.validateUserAndServiceCode(
                                        rp, user, serviceCode, "1", rq.getLang(), null);
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        // Kiểm tra quyền giao dịch tài khoản nguồn
                        rp =
                                transactionService.validateTransAuthorityForAccount(
                                        rp, user, debitAccountNo, serviceCode, rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        // Kiểm tra mã sản phẩm tài khoản
                        rp =
                                transactionService.validateAccountProduct(
                                        rp, serviceCode, debitBankResp.getProductCode(), null, rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        rp =
                                transactionService.checkDebitAccountStatus(
                                        rp, debitAccountNo, "D", debitAccountNo.length() > 10, true, rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        String endpoint = env.getProperty("bilateral.endpoint.inquiry");
                        String userAuth = env.getProperty("bilateral.basicAuthen.user");
                        String passAuth = env.getProperty("bilateral.basicAuthen.pass");


                        // Lấy thông tin credit account
//                        CheckBene247ViaCardBankRequest bankRq =
//                                CheckBene247ViaCardBankRequest.builder()
//                                        .cardToken(rq.getToAcc())
//                                        .benBin(rq.getToAcc().substring(0, 6))
//                                        .build();

                        RequestContentAcc247Inquiry requestContentAcc247 = RequestContentAcc247Inquiry.builder()
                                .account(rq.getToAcc())
                                .card(true)
                                .token(true).build();
                        Bene247AccOutInquiryRequest bankRq = Bene247AccOutInquiryRequest.builder()
                                .requestContent(requestContentAcc247).build();
                        String bankResponseInquiry =
                                restClient.post(endpoint, gson.toJson(bankRq), userAuth, passAuth);
                        Bene247AccOutInquiryResponse creditAccountInfo = gson.fromJson(bankResponseInquiry, Bene247AccOutInquiryResponse.class);
                        if (!"00".equals(creditAccountInfo.getResponseStatus().getResponseCode())) {
                            if (creditAccountInfo.getResponseContent() != null && creditAccountInfo.getResponseContent().getRoute() != 0) {
                                rp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + creditAccountInfo.getResponseContent().getRoute() + "-" + creditAccountInfo.getResponseStatus().getResponseCode());
                                rp.setMessage(commonService.getMessage(rp.getCode(), rq.getLang()));
                            } else {
                                rp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + creditAccountInfo.getResponseStatus().getResponseCode());
                                rp.setMessage(commonService.getMessage(rp.getCode(), rq.getLang()));
                            }
                            return rp;
                        }
//                        CheckBene247BankResponse creditAccountInfo =
//                                tranfer247Client.checkBene247ViaCard(bankRq);

//                        if (!"0".equals(creditAccountInfo.getResponseStatus().getResCode())) {
//                            String code = "0199";
//                            if (creditAccountInfo.getResponseStatus().getIsFail()) {
//                                code = creditAccountInfo.getResponseStatus().getResCode();
//                            }
//                            rp.setCode(code);
//                            rp.setMessage(commonService.getMessage("CARD-TRANSFER-" + code, rq.getLang()));
//                            return rp;
//                        }

                        // Lấy phương thức xác thực
                        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
                        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
                            rp.setCode(checkAuthen.getCode());
                            rp.setMessage(commonService.getMessage(checkAuthen.getCode(), rq.getLang()));
                            return rp;
                        }

                        String authenType = checkAuthen.getAuthenMethod();
                        String tranToken = transactionService.genTranToken();
                        dataRp.setTranToken(tranToken);
                        dataRp.setAuthenType(authenType);
                        dataRp.setServiceCode(Constants.ServiceCode.FAST_TRANS_VIA_ACCNO);

                        BigDecimal exchangeRate = BigDecimal.ONE;
                        BigDecimal exchangeRateUSD = BigDecimal.ONE;
                        ExchangeRateInquiryBankResponse tiGiaNgoaiTe = null;
                        ExchangeRateInquiryBankResponse tiGiaUsd = null;
                        if (!"VND".equals(debitBankResp.getCurCode())) {
                            ExchangeRateInquiryBankResponse exchangeRateRes =
                                    coreQueryClient.getExchangeRateInquiry(
                                            ExchangeRateInquiryBankRequest.builder()
                                                    .currency(debitBankResp.getCurCode())
                                                    .build());
                            if (exchangeRateRes.getResponseStatus().getIsFail()) {
                                rp.setCode(exchangeRateRes.getResponseStatus().getResCode());
                                rp.setMessage(
                                        commonService.getMessage(
                                                exchangeRateRes.getResponseStatus().getResCode(), rq.getLang()));
                                return rp;
                            }
                            tiGiaNgoaiTe = exchangeRateRes;
                            exchangeRate = exchangeRateRes.getAppXferBuy();
                            if ("USD".equals(debitBankResp.getCurCode())) {
                                exchangeRateUSD = exchangeRate;
                            } else {
                                exchangeRateRes =
                                        coreQueryClient.getExchangeRateInquiry(
                                                ExchangeRateInquiryBankRequest.builder().currency("USD").build());
                                if (exchangeRateRes.getResponseStatus().getIsFail()) {
                                    rp.setCode(exchangeRateRes.getResponseStatus().getResCode());
                                    rp.setMessage(
                                            commonService.getMessage(
                                                    exchangeRateRes.getResponseStatus().getResCode(), rq.getLang()));
                                    return rp;
                                }
                                //exchangeRateUSD = exchangeRateRes.getAppXferBuy();
                                exchangeRateUSD = exchangeRateRes.getSellRate();
                                tiGiaUsd = exchangeRateRes;
                            }
                        }

                        BigDecimal amount;
                        BigDecimal exchangeAmount;
                        if ("VND".equals(rq.getCurCode())) {
                            exchangeAmount = new BigDecimal(rq.getAmount());
                            amount = exchangeAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);

                        } else {
                            amount = new BigDecimal(rq.getAmount());
                            exchangeAmount = exchangeRate.multiply(amount);
                        }

                        // Tính phí giao dịch
                        String vatExamptFlag = commonService.getVatExemptFlag(rq);
                        GetFeeTransferDTO input = GetFeeTransferDTO.builder()
                                .accountPkgCode(user.getAccountPkgCode())
                                .pkgCode(user.getPackageCode())
                                .promCode(user.getValidPromCode())
                                .serviceCode(serviceCode)
                                .authMethod(authenType)
                                .ccy(debitBankResp.getCurCode())
                                .amount(amount)
                                .exchangeAmount(exchangeAmount)
                                .isExamptVat("Y".equalsIgnoreCase(vatExamptFlag))
                                .creditAccount(rq.getToAcc())
                                .tiGiaUsd(tiGiaUsd)
                                .tiGiaNgoaiTe(tiGiaNgoaiTe)
                                .build();
                        FeeTransferDTO feeDto = transactionFeeService.getFeeTransfer(input);
                        BigDecimal exchangeFee, exchangeVat, feeV, vatV, feeU, vatU;
                        if ("VND".equals(feeDto.getCcy())) {
                            exchangeFee = feeDto.getFee();
                            exchangeVat = feeDto.getVat();
                            feeV = exchangeFee.divide(exchangeRate, 2, RoundingMode.HALF_UP);
                            vatV = exchangeVat.divide(exchangeRate, 2, RoundingMode.HALF_UP);
                            feeU = vatU = null;
                        } else {
                            feeU = feeDto.getFee();
                            vatU = feeDto.getVat();
                            exchangeFee = feeU.multiply(exchangeRateUSD).setScale(0, RoundingMode.HALF_UP);;
                            exchangeVat = vatU.multiply(exchangeRateUSD).setScale(0, RoundingMode.HALF_UP);;
                            feeV = exchangeFee.divide(exchangeRate, 2, RoundingMode.HALF_UP);
                            vatV = exchangeVat.divide(exchangeRate, 2, RoundingMode.HALF_UP);
                        }

                        BigDecimal tranxAmount = exchangeAmount;
                        BigDecimal debitAmount, creditAmount, debitExchangeAmount;
                        if ("1".equals(rq.getFeeType())) {
                            debitAmount = amount.add(feeV).add(vatV).setScale(2, RoundingMode.HALF_UP);
                            debitExchangeAmount = tranxAmount.add(exchangeFee).add(exchangeVat);
                            creditAmount = tranxAmount;
                        } else {
                            debitAmount = amount.setScale(2, RoundingMode.HALF_UP);
                            debitExchangeAmount = exchangeAmount;
                            creditAmount = exchangeAmount.subtract(exchangeFee).subtract(exchangeVat);
                        }

                        // Convert tiền tệ khác USD, và VND để tính toàn hạn mức
                        CheckLimitTrans checkLimitTrans =
                                transactionLimitService.convertCcyAmount(
                                        rp, debitBankResp.getCurCode(), amount, exchangeAmount);
                        if (!Constants.ResCode.INFO_00.equals(rp.getCode())) {
                            return rp;
                        }
                        // Kiểm tra hạn mức giao dịch chung

                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(), user.getUsername(), serviceCode, rq.getFromAcc());
                        BaseClientResponse checkLimit =
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

                        // Kiểm tra số dư tại khoản debit
                        String minBal =
                                debitBankResp.getCurCode().equals("VND")
                                        ? commonService.getConfig("MIN_BALANCE", "50000")
                                        : commonService.getConfig("MIN_BALANCE_FOR", "10");

                        if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                .subtract(new BigDecimal(minBal))
                                .doubleValue()
                                < debitAmount.doubleValue()) {
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
                            rp.setCode(Constants.ResCode.ERROR_113);
                            rp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_113, rq.getLang()));
                            return rp;
                        }

                        dataRp.setToAccName(Strings.nullToEmpty(rq.getToAccName()));

                        long transId = smeTransRepository.getNextValSmeTransSeq().longValue();

                        // Lưu dữ liệu giao dich
                        SmeTrans smeTrans = new SmeTrans();
                        smeTrans.setId(transId);
                        smeTrans.setCusName(user.getCusName());
                        smeTrans.setCreatedUser(user.getUsername());
                        smeTrans.setCreatedMobile(user.getMobileOtp());
                        smeTrans.setFromAcc(
                                Strings.isNullOrEmpty(debitBankResp.getAccountAlias())
                                        ? debitBankResp.getAccountNo()
                                        : debitBankResp.getAccountAlias());
                        smeTrans.setToAcc(rq.getCardMaskingNumber());
                        smeTrans.setTranxType(Constants.ServiceCode.FAST_TRANS_VIA_CARDNO);
                        smeTrans.setTranxNote("Maker init");
                        smeTrans.setCifNo(user.getCif());
                        smeTrans.setTranxTime(LocalDateTime.now());
                        smeTrans.setChannel(rq.getSource());
                        smeTrans.setMakerAuthenType(authenType);
                        smeTrans.setTranxContent(rq.getContent());
                        String channel = rq.getSource().equals("MB") ? "MBBIZ" : "IBBIZ";
                        String remark =
                                channel
                                        + "$1.CT tu "
                                        + rq.getFromAcc()
                                        + " toi "
                                        + rq.getCardMaskingNumber()
                                        + "."
                                        + rq.getContent();
                        smeTrans.setTranxRemark(remark.replace("$1", String.valueOf(transId)));
                        smeTrans.setCcy(rq.getCurCode());
                        smeTrans.setStatus(Constants.TransStatus.MAKER_WAIT_CONFIRM);
                        smeTrans.setFeeType(rq.getFeeType());
                        smeTrans.setCreditName(dataRp.getToAccName());
                        smeTrans.setBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setAmount(exchangeAmount.doubleValue());
                        smeTrans.setRealAmount(debitExchangeAmount);
                        // Thong tin thu huong
                        smeTrans.setCreditName(creditAccountInfo.getResponseContent().getName());
                        smeTrans.setBeneBankCode(rq.getBeneBankCode());
                        smeTrans.setFeeOnAmt(exchangeVat.doubleValue());
                        smeTrans.setFlatFee(exchangeFee.doubleValue());
                        smeTrans.setDebitBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setCifInt(user.getCifInt());
                        smeTrans.setAuthenType(authenType);

                        // Thực hiện chuyển khoản
                        DebitAccountDTO debitAccount =
                                DebitAccountDTO.builder()
                                        .accountHolderName(debitBankResp.getAccountName())
                                        .accountNo(debitAccountNo)
                                        .amountVND(debitExchangeAmount.longValue())
                                        .originAmount(debitAmount.doubleValue())
                                        .accountType("D")
                                        .branch(smeTrans.getBranchCode())
                                        .currency(debitBankResp.getCurCode())
                                        .cif(Integer.parseInt(smeTrans.getCifNo()))
                                        .rate(String.valueOf(exchangeRate))
                                        .accountAddress(debitBankResp.getAccountAddress())
                                        .accountName(debitBankResp.getAccountName())
                                        .panNumber(debitCard.getVcbToken())
                                        .panToken(true)
                                        .build();

                        DebitCardDTO debitCardTransfer =
                                DebitCardDTO.builder()
                                        .panHash(debitCard.getPanHash())
                                        .cardNumber(debitCard.getCardNumber())
                                        .issueBranch(debitCard.getBranch() + debitCard.getSubBranch())
                                        .vcbToken(debitCard.getVcbToken())
                                        .build();

                        CreditAccountDTO creditAccount =
                                CreditAccountDTO.builder()
                                        .accountHolderName(smeTrans.getCreditName())
                                        .accountNo(rq.getToAcc())
                                        .amountVND(creditAmount.longValue())
                                        .originAmount(creditAmount.longValue())
                                        .currency("VND")
                                        .rate("1")
                                        .bankCode(rq.getBeneBankCode())
                                        .bankName(Strings.nullToEmpty(rq.getBeneBankName()))
                                        .accountName(creditAccountInfo.getResponseContent().getName())
                                        .panNumber(rq.getToAcc())
                                        .panToken(true)
                                        .build(); // Tài khoản đích

                        FeeDTO fee =
                                FeeDTO.builder()
                                        .amount(exchangeFee.longValue())
                                        .amountVND(exchangeFee.longValue())
                                        .vatAmountVND(exchangeVat.longValue())
                                        .authMethod(0)
                                        .currency(debitAccount.getCurrency())
                                        .originAmount(feeV.doubleValue())
                                        .originAuthMethod(0)
                                        .originVatAmount(vatV.doubleValue())
                                        .type(Integer.parseInt(rq.getFeeType()))
                                        .vatAmount(exchangeVat.longValue())
                                        .chargeType("2")
                                        .build(); // Phí

                        // kiem tra loai tien te co cho phep so le thap phan
                        if (cmCurrency != null && !"vnd".equalsIgnoreCase(debitAccount.getCurrency()) &&
                                "0".equals(cmCurrency.getIsDecimal())) {
                            // kiem tra neu amount request client truyen len la so thap phan
                            if (!captchaService.isNotDecimalDigits(Double.valueOf(rq.getAmount()))) {
                                log.error("amount is decimal value");
                                rp.setCode(Constants.ResCode.ERROR_96);
                                rp.setMessage("amount is decimal value");

                                return rp;
                            }
                            log.info("rounding decimal amount");
                            BigDecimal originAmount = BigDecimal.valueOf(debitAccount.getOriginAmount());
                            BigDecimal amountVnd = BigDecimal.valueOf(debitAccount.getAmountVND());
                            debitAccount.setOriginAmount(originAmount.setScale(0, RoundingMode.HALF_UP).doubleValue());
                            debitAccount.setAmountVND(amountVnd.setScale(0, RoundingMode.HALF_UP).doubleValue());

                            BigDecimal originAmountCre = BigDecimal.valueOf(creditAccount.getOriginAmount());
                            BigDecimal amountVndCre = BigDecimal.valueOf(creditAccount.getAmountVND());
                            creditAccount.setOriginAmount(originAmountCre.setScale(0, RoundingMode.HALF_UP).doubleValue());
                            creditAccount.setAmountVND(amountVndCre.setScale(0, RoundingMode.HALF_UP).doubleValue());

                            BigDecimal originAmountFee = BigDecimal.valueOf(fee.getOriginAmount());
                            BigDecimal originVatAmountFee = BigDecimal.valueOf(fee.getOriginVatAmount());
                            fee.setOriginAmount(originAmountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
                            fee.setOriginVatAmount(originVatAmountFee.setScale(0, RoundingMode.HALF_UP).doubleValue());
                            amount = amount.setScale(0, RoundingMode.HALF_UP);

                            double notBal = Math.abs(amount.doubleValue() + fee.getOriginAmount() + fee.getOriginVatAmount() - debitAccount.getOriginAmount());
                            if (amount.doubleValue() + fee.getOriginAmount() + fee.getOriginVatAmount() > debitAccount.getOriginAmount()) {
                                debitAccount.setOriginAmount(debitAccount.getOriginAmount() + notBal);
                            } else {
                                //amount = BigDecimal.valueOf(amount.doubleValue() + notBal);
                                debitAccount.setOriginAmount(debitAccount.getOriginAmount() - notBal);
                            }
                        }

                        TransactionMetaDataDTO metaDataDTO =
                                TransactionMetaDataDTO.builder()
                                        .beneBankName(Strings.nullToEmpty(rq.getBeneBankName()))
                                        .debitAccountNo(debitBankResp.getAccountNo())
                                        .debitAccount(debitAccount)
                                        .creditAccount(creditAccount)
                                        .debitCard(debitCardTransfer)
                                        .amountVND(exchangeAmount.longValue())
                                        .originAmount(amount.doubleValue())
                                        .fee(fee)
                                        .adviceRoute(creditAccountInfo.getResponseContent().getRoute())
                                        .exchangeTotalFee(exchangeFee.add(exchangeVat).doubleValue())
                                        .totalFee(feeV.add(vatV).doubleValue())
                                        .feeU(feeU)
                                        .vatU(vatU)
                                        .clientRqCcy(rq.getCurCode())
                                        .build();
                        metaDataDTO.setCheckLimitTrans(checkLimitTrans);
                        metaDataDTO.setDebitName(debitBankResp.getAccountName());
                        metaDataDTO.setDebitAddr(debitBankResp.getAccountAddress());
                        metaDataDTO.setVatExamptFlag(vatExamptFlag);
                        smeTrans.setMetadata(gson.toJson(metaDataDTO));

                        SmeTrans savedSmeTrans = smeTransRepository.save(smeTrans);

                        // Lưu thêm chi tiết giao dịch
                        SmeTransactionDetail transactionDetail = new SmeTransactionDetail();
                        transactionDetail.setTranxId(savedSmeTrans.getId());
                        transactionDetail.setTranxPhase(Constants.TransPhase.MAKER_INIT);
                        transactionDetail.setResCode("00");
                        transactionDetail.setCreatedDate(LocalDateTime.now());
                        transactionDetail.setTranxNote("Khoi tao thanh cong");
                        transactionDetail.setSource(rq.getSource());
                        smeTransDetailRepository.save(transactionDetail);

                        // Khởi tạo phương thức xác thực
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        authenType,
                                        rq.getFromAcc(),
                                        rq.getToAcc(),
                                        savedSmeTrans.getId(),
                                        rq.getAmount(),
                                        tranToken,
                                        dataRp.getServiceCode(),
                                        "",
                                        rq.getCurCode());
                        if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                            log.error("init authen error: " + intAuthen.getCode());
                            rp.setCode(intAuthen.getCode());
                            rp.setMessage(intAuthen.getMessage());
                            return rp;
                        }
                        savedSmeTrans.setAuthenType(authenType);
                        savedSmeTrans.setChallenge(intAuthen.getDataAuthen());
                        dataRp.setTranxId(String.valueOf(savedSmeTrans.getId()));
                        dataRp.setDataAuthen(intAuthen.getDataAuthen());
                        dataRp.setTranToken(tranToken);
                        dataRp.setFee(feeV.doubleValue());
                        dataRp.setVat(vatV.doubleValue());
                        dataRp.setTotalFee(feeV.add(vatV).doubleValue());
                        if ("VND".equals(feeDto.getCcy())) {
                            dataRp.setExchangeFee(exchangeFee);
                            dataRp.setExchangeVat(exchangeVat);
                            dataRp.setExchangeTotalFee(exchangeFee.add(exchangeVat));
                        }
                        if (feeU != null) {
                            dataRp.setFeeU(feeU);
                            dataRp.setVatU(vatU);
                            dataRp.setTotalFeeU(feeU.add(vatU));
                        }
                        dataRp.setExchangeTotalAmount(debitExchangeAmount.longValue());
                        dataRp.setToAccName(smeTrans.getCreditName());
                        dataRp.setServiceCode(smeTrans.getTranxType());
                        dataRp.setClientRqCcy(rq.getCurCode());
                        dataRp.setAmount(amount.doubleValue());
                        dataRp.setExchangeAmount(exchangeAmount.longValue());
                        dataRp.setExchangeRate(exchangeRate.longValue());

                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, savedSmeTrans);

                        // Trả ra dữ liệu cho client
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

    public BaseClientResponse makerConfirmViaCard(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));

        boolean isDeleteCache = true;
        try {
            boolean isExe = !redisCacheService.setnx(req.getTranxId(), 15l);
            if (isExe) {
                log.error("trans duplicate");
                isDeleteCache = false;
                String resCode = "028";
                return new BaseClientResponse(
                        resCode,
                        commonService.getMessage("DUPL-028", req.getLang()));
            }

            SmeCustomerUser user = redisCacheService.getCustomerUser(req);
            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        log.info("BaseConfirmRq: {}", gson.toJson(req));
                        // Valid giao dich
                        BaseClientResponse valid = transactionService.validTxn(req);
                        if (!Constants.ResCode.INFO_00.equals(valid.getCode())) {
                            return valid;
                        }
                        BaseTransactionResponse data = new BaseTransactionResponse();

                        // Get cache value
                        String cacheTrans = redisCacheService.getTxn(req);
                        log.info("Cache transaction: " + cacheTrans);
                        SmeTrans cachedSmeTrans = gson.fromJson(cacheTrans, SmeTrans.class);

                        if (cachedSmeTrans.isRequestProcessed()) {
                            log.error("trans duplicate");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }

                        Optional<SmeTrans> optSmeTrans = smeTransRepository.findById(cachedSmeTrans.getId());
                        if (!Constants.TransStatus.MAKER_WAIT_CONFIRM.equals(optSmeTrans.get().getStatus())) {
                            log.info("Invalid trans status: {}", cachedSmeTrans.getStatus());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        if (!cachedSmeTrans.getMakerAuthenType().equals(req.getAuthenType())) {
                            log.info("Invalid authenType: {}", req.getAuthenType());
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

                        // Kiếm tra role type được quyền thực hiện chức năng hay không
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, cachedSmeTrans.getTranxType(), "1", req.getLang(), null);
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
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
                                        Strings.nullToEmpty(req.getChallenge()),
                                        String.valueOf(cachedSmeTrans.getAmount()),
                                        cachedSmeTrans.getServiceCode(),
                                        cachedSmeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(initAuthenResponse.getCode())) {

                            transactionDetail.setResCode(initAuthenResponse.getCode());
                            transactionDetail.setResDesc(initAuthenResponse.getMessage());
                            transactionDetail.setTranxNote("Maker confirm failed");
                            transactionDetail.setDetail("Authenticate failed");
                            smeTransDetailRepository.save(transactionDetail);

                            resp.setCode(initAuthenResponse.getCode());
                            resp.setMessage(initAuthenResponse.getMessage());
                            return resp;
                        }

                        String metaStr = cachedSmeTrans.getMetadata();
                        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);
                        // Kiểm tra giao dịch đi ngay hay chờ duyệt
                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(),
                                        user.getUsername(),
                                        cachedSmeTrans.getTranxType(),
                                        metaData.getDebitAccountNo());
                        log.info("=====: " + cachedSmeTrans.getBranchCode());
                        // Xử lý lưu giao dich để check hạn mức
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        String type;
                        if (isExecTrans) {
                            GetBankHostDateResponse hostDateResponse =
                                    coreQueryClient.getHostDate(new BaseBankRequest());
                            type = "2";
                            resp =
                                    transactionService.execTransfer247ViaCard(
                                            resp, cachedSmeTrans, hostDateResponse.getCurrentDate(), req);
                            if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                                transactionDetail.setResCode(resp.getCode());
                                transactionDetail.setTranxNote(resp.getMessage());
                                transactionDetail.setDetail(resp.getMessage());
                                smeTransDetailRepository.save(transactionDetail);
                                return resp;
                            }

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
                        cachedSmeTrans.setRequestProcessed(true);
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans); //update cache trans

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
                                        metaData.getCreditAccount().getAccountNo(),
                                        null,
                                        null);

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranxId(String.valueOf(cachedSmeTrans.getId()));
                        dataRp.setTranDate(CommonUtils.formatDate(new Date()));
                        dataRp.setIsExecTrans(isExecTrans ? "1" : "0");
                        dataRp.setContact(isContact);
                        dataRp.setClientRqCcy(metaData.getClientRqCcy());

                        // return transaction info
                        dataRp.setAmount(metaData.getOriginAmount());
                        dataRp.setExchangeAmount((long) metaData.getAmountVND());

                        resp.setData(dataRp);
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
        }
        finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }
}
