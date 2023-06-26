package vn.vnpay.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.*;
import vn.vnpay.commoninterface.request.BaseCheckerInitRequest;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.request.SendEmailCommonRequest;
import vn.vnpay.commoninterface.response.AuthenMethodResponse;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.commoninterface.response.InitAuthenResponse;
import vn.vnpay.commoninterface.service.*;
import vn.vnpay.config.MessagingConfig;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.entity.*;
import vn.vnpay.dbinterface.repository.BinCardConfigRepository;
import vn.vnpay.dbinterface.repository.CmCurrencyRepository;
import vn.vnpay.dbinterface.repository.SmeTransDetailRepository;
import vn.vnpay.dbinterface.repository.SmeTransRepository;
import vn.vnpay.request.CheckBene247Request;
import vn.vnpay.request.CheckCutOffTimeRequest;
import vn.vnpay.request.InitConfirmTransBatchRequest;
import vn.vnpay.request.TransferOutRequest;
import vn.vnpay.response.TransConfirmRes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransferOutService {
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
    private MiscClient miscClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransactionFeeService transactionFeeService;

    @Autowired
    private BinCardConfigRepository binCardConfigRepository;

    @Autowired
    Transfer247ViaAccountService transfer247Service;

    @Autowired
    private Tranfer247Client tranfer247Client;

    @Autowired
    private RedisCacheService cacheService;

    @Autowired
    @Qualifier(value = "firstRabbitTemplate")
    public RabbitTemplate firstRabbitTemplate;

    @Autowired
    SmeApiServiceClient smeApiServiceClient;

    @Autowired
    private CmCurrencyRepository cmCurrencyRepository;

    @Autowired
    private CaptchaService captchaService;

    public BaseClientResponse checkCutOffTime(CheckCutOffTimeRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String date = CommonUtils.format("yyyyMMdd", new Date());
            if (Constants.ServiceCode.LOAN_PAYMENT.equals(rq.getServiceCode())) {
                if (commonService.checkDuringTime(Strings.nullToEmpty(rq.getServiceCode()))) {
                    rp.setCode(Constants.ResCode.INFO_76);
                    rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_76, rq.getLang()));
                }
                return rp;
            }
            if (commonService.checkDuringTime(Strings.nullToEmpty(rq.getServiceCode()))
                    || (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)) {
                rp.setCode("33");
                rp.setMessage(commonService.getMessage("CUTOFF-33", rq.getLang()));
                return rp;
            }
        } catch (Exception e) {
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return rp;
    }

    public BaseClientResponse makerInit(TransferOutRequest rq) {
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
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Kiem tra bin credit
                        List<BinCard> listBin = binCardConfigRepository.findByStatus("1");
                        log.info("listBin: " + gson.toJson(listBin));
                        if (listBin != null && rq.getToAcc().length() > 6) {
                            for (BinCard b : listBin) {
                                log.info("listBin: " + gson.toJson(b) + "|" + rq.getToAcc().substring(0, 6));
                                if (b.getBinValue().contains(rq.getToAcc().substring(0, 6))) {
                                    rp.setCode(Constants.ResCode.ERROR_114);
                                    rp.setMessage(
                                            commonService.getMessage(Constants.MessageCode.ERROR_114, rq.getLang()));
                                    return rp;
                                }
                            }
                        }

                        // Kiểm tra tài khoản debit
                        List<AccountDTO> listAccount = user.getListAccount();

                        Optional<AccountDTO> opt =
                                listAccount.stream()
                                        .filter(
                                                x ->
                                                        (rq.getFromAcc().equalsIgnoreCase(x.getAccountNo())
                                                                || rq.getFromAcc().equalsIgnoreCase(x.getAccountAlias())))
                                        .findFirst();

                        if (!opt.isPresent()) {
                            rp.setCode(Constants.ResCode.INFO_23);
                            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return rp;
                        }
                        AccountDTO acc = opt.get();

                        log.info("====== " + rq.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));

                        // Lấy thông tin mã sản phẩm tài khoản debit
                        log.info("Get Debit account details for {}", rq.getFromAcc());
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(rq.getFromAcc());
                        accDetailsReq.setAccountType("D");
                        accDetailsReq.setAlias(rq.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));
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

                        rp =
                                transactionService.checkDebitAccountStatus(
                                        rp,
                                        rq.getFromAcc(),
                                        acc.getAccountType(),
                                        rq.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()),
                                        true,
                                        rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        String remark = "";
                        String channel = rq.getSource().equals("MB") ? "MBBIZ" : "IBBIZ";
                        String serviceCode;
                        switch (rq.getTransType()) {
                            case "2": // Tương lai
                                serviceCode = Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE;
                                remark =
                                        channel
                                                + "$1.IBPS.CTTL."
                                                + CommonUtils.TimeUtils.getNow("dd/MM/yyyy")
                                                + "."
                                                + rq.getContent();
                                break;
                            case "3": // Định kỳ
                                serviceCode = Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED;
                                remark =
                                        channel
                                                + "$1.IBPS.CTDK."
                                                + CommonUtils.TimeUtils.getNow("dd/MM/yyyy")
                                                + "."
                                                + rq.getContent();
                                break;
                            default: // Ngày hiện tại
                                serviceCode = Constants.ServiceCode.TRANS_OUT_VIA_ACCNO;
                                remark = channel + "$1.IBPS." + rq.getContent();
                                break;
                        }
                        if (CommonUtils.isRemarkContainsSpecialChars(remark)) {
                            log.info("Remark không hợp lệ ====> chứa ký tự đặc biệt");
                            return commonService.makeClientResponse(Constants.ResCode.INFO_64, commonService.getMessage(Constants.MessageCode.INFO_64, rq.getLang()));
                        }

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
                                        rp, user, rq.getFromAcc(), serviceCode, rq.getLang());
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
                        dataRp.setServiceCode(serviceCode);

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

                        // Convert tiền tệ khác USD, và VND để tính toàn hạn mức
                        CheckLimitTrans checkLimitTrans =
                                transactionLimitService.convertCcyAmount(
                                        rp, acc.getCurCode(), amount, exchangeAmount);
                        if (!Constants.ResCode.INFO_00.equals(rp.getCode())) {
                            return rp;
                        }
                        // Kiểm tra hạn mức giao dịch chung

                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(), user.getUsername(), serviceCode, rq.getFromAcc());
                        BaseClientResponse checkLimit;
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(serviceCode)) {
                            checkLimit =
                                    transactionLimitService.checkTranLimit(
                                            user,
                                            rq,
                                            serviceCode,
                                            checkLimitTrans.getAmount(),
                                            checkLimitTrans.getCcy(),
                                            authenType,
                                            isExecTrans,
                                            rq.getFutureDate());
                        } else {
                            checkLimit =
                                    transactionLimitService.checkTranLimit(
                                            user,
                                            rq,
                                            serviceCode,
                                            checkLimitTrans.getAmount(),
                                            checkLimitTrans.getCcy(),
                                            authenType,
                                            isExecTrans);
                        }

                        if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                            log.error("Lỗi hạn mức");
                            return checkLimit;
                        }

                        // Tính phí giao dịch
                        String vatExamptFlag = commonService.getVatExemptFlag(rq);
                        GetFeeTransferDTO input = GetFeeTransferDTO.builder()
                                .accountPkgCode(user.getAccountPkgCode())
                                .pkgCode(user.getPackageCode())
                                .promCode(user.getValidPromCode())
                                .serviceCode(serviceCode)
                                .authMethod(authenType)
                                .ccy(acc.getCurCode())
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
                            feeU = null;
                            vatU = null;
                        } else {
                            feeU = feeDto.getFee();
                            vatU = feeDto.getVat();
                            exchangeFee = feeU.multiply(exchangeRateUSD).setScale(0, RoundingMode.HALF_UP);
                            exchangeVat = vatU.multiply(exchangeRateUSD).setScale(0, RoundingMode.HALF_UP);
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

                        // Kiểm tra số dư tại khoản debit với cache chuyển tiền ngay
                        if ("1".equals(rq.getTransType())) {
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
                        }

                        if (creditAmount.longValue() < 0) {
                            rp.setCode(Constants.ResCode.ERROR_113);
                            rp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_113, rq.getLang()));
                            return rp;
                        }

                        long transId = smeTransRepository.getNextValSmeTransSeq().longValue();

                        // Lưu dữ liệu giao dich
                        SmeTrans smeTrans = new SmeTrans();
                        smeTrans.setId(transId);
                        smeTrans.setFeeOnAmt(feeDto.getVat().doubleValue());
                        smeTrans.setFlatFee(feeDto.getFee().doubleValue());
                        smeTrans.setTotalAmount(debitAmount.doubleValue());
                        dataRp.setToAccName(Strings.nullToEmpty(rq.getToAccName()));
                        smeTrans.setCusName(user.getCusName());
                        smeTrans.setCreatedUser(user.getUsername());
                        smeTrans.setCreatedMobile(user.getMobileOtp());
                        smeTrans.setFromAcc(rq.getFromAcc());
                        smeTrans.setToAcc(rq.getToAcc());
                        smeTrans.setBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setTranxType(serviceCode);
                        smeTrans.setTranxNote("Maker init");
                        smeTrans.setCifNo(user.getCif());
                        smeTrans.setTranxTime(LocalDateTime.now());
                        smeTrans.setChannel(rq.getSource());
                        smeTrans.setMakerAuthenType(authenType);
                        smeTrans.setTranxContent(rq.getContent());
                        smeTrans.setTranxRemark(remark.replace("$1", String.valueOf(transId)));
                        smeTrans.setCcy(rq.getCurCode());
                        smeTrans.setStatus(Constants.TransStatus.MAKER_WAIT_CONFIRM);
                        smeTrans.setFeeType(rq.getFeeType());
                        smeTrans.setFeeOnAmt(exchangeVat.doubleValue());
                        smeTrans.setFlatFee(exchangeFee.doubleValue());
                        smeTrans.setCreditName(dataRp.getToAccName());
                        smeTrans.setTranxContent(rq.getContent());
                        smeTrans.setBeneBankCode(rq.getBeneBankCode());
                        smeTrans.setAmount(exchangeAmount.doubleValue());
                        smeTrans.setDebitBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setCifInt(user.getCifInt());
                        smeTrans.setRealAmount(debitExchangeAmount);
                        smeTrans.setAuthenType(authenType);

                        // Thực hiện chuyển khoản
                        DebitAccountDTO debitAccount =
                                DebitAccountDTO.builder()
                                        .accountHolderName(debitBankResp.getAccountName())
                                        .accountNo(smeTrans.getFromAcc())
                                        .amountVND(debitExchangeAmount.longValue())
                                        .originAmount(debitAmount.doubleValue())
                                        .accountType("D")
                                        .branch(smeTrans.getBranchCode())
                                        .currency(debitBankResp.getCurCode())
                                        .cif(Integer.parseInt(smeTrans.getCifNo()))
                                        .rate(String.valueOf(exchangeRate))
                                        .build();

                        String creditBankCode =
                                Strings.isNullOrEmpty(rq.getBeneBranchCode())
                                        ? rq.getBeneBankCode()
                                        : rq.getBeneBranchCode();
                        CreditAccountDTO creditAccount =
                                CreditAccountDTO.builder()
                                        .accountHolderName(smeTrans.getCreditName())
                                        .accountNo(smeTrans.getToAcc())
                                        .amountVND(creditAmount.longValue())
                                        .originAmount(creditAmount.longValue())
                                        .currency("VND")
                                        .rate("1")
                                        .bankCode(creditBankCode)
                                        .branch(creditBankCode)
                                        .bankName(Strings.nullToEmpty(rq.getBeneBankName()))
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
                                        .build(); // Phí

                        CronJobDTO cronJob =
                                CronJobDTO.builder()
                                        .specificDate(rq.getFutureDate())
                                        .endDate(rq.getToDate())
                                        .interval(rq.getInterval())
                                        .startDate(rq.getFromDate())
                                        .unit(
                                                Integer.parseInt(
                                                        Strings.isNullOrEmpty(rq.getIntervalUnit())
                                                                ? "0"
                                                                : rq.getIntervalUnit()))
                                        .build();

                        FutureTransDataDTO futureTransData =
                                FutureTransDataDTO.builder()
                                        .makerId(rq.getUser())
                                        .build();

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

                        // Thong tin thu huong
                        smeTrans.setCreditName(Strings.nullToEmpty(rq.getToAccName()));
                        smeTrans.setBeneBankCode(rq.getBeneBankCode());
                        smeTrans.setBeneBranchCode(rq.getBeneBranchCode());
                        TransactionMetaDataDTO metaDataDTO =
                                TransactionMetaDataDTO.builder()
                                        .beneCityCode(Strings.nullToEmpty(rq.getBeneCityCode()))
                                        .beneBankName(Strings.nullToEmpty(rq.getBeneBankName()))
                                        .beneCityName(Strings.nullToEmpty(rq.getBeneCityName()))
                                        .beneBranchName(Strings.nullToEmpty(rq.getBeneBranchName()))
                                        .futureDate(rq.getFutureDate())
                                        .intervalUnit(rq.getIntervalUnit())
                                        .fromDate(rq.getFromDate())
                                        .toDate(rq.getToDate())
                                        .debitAccount(debitAccount)
                                        .creditAccount(creditAccount)
                                        .fee(fee)
                                        .originAmount(amount.doubleValue())
                                        .amountVND(exchangeAmount.longValue())
                                        .cronJob(cronJob)
                                        .exchangeTotalFee(exchangeFee.add(exchangeVat).doubleValue())
                                        .totalFee(feeV.add(vatV).doubleValue())
                                        .futureTransData(futureTransData)
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
                        dataRp.setFee(fee.getOriginAmount());
                        dataRp.setVat(fee.getOriginVatAmount());
                        dataRp.setTotalFee(fee.getOriginAmount() + fee.getOriginVatAmount());
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

    public BaseClientResponse makerConfirm(BaseConfirmRq req) {
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

            log.info("BaseConfirmRq: {}", gson.toJson(req));
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
                        String cacheTrans = redisCacheService.getTxn(req);
                        log.info("Cache transaction: " + cacheTrans);
                        SmeTrans cachedSmeTrans = gson.fromJson(cacheTrans, SmeTrans.class);

                        //check block sync request
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

                        TransactionMetaDataDTO metadata =
                                gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(), user.getUsername(), cachedSmeTrans.getTranxType(), cachedSmeTrans.getFromAcc());
                        BaseClientResponse checkLimit;
                        CheckLimitTrans checkLimitTrans = metadata.getCheckLimitTrans();
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(cachedSmeTrans.getTranxType())) {
                            checkLimit =
                                    transactionLimitService.checkTranLimit(
                                            user,
                                            req,
                                            cachedSmeTrans.getTranxType(),
                                            checkLimitTrans.getAmount(),
                                            checkLimitTrans.getCcy(),
                                            cachedSmeTrans.getMakerAuthenType(),
                                            isExecTrans,
                                            metadata.getFutureDate());
                        } else {
                            checkLimit =
                                    transactionLimitService.checkTranLimit(
                                            user,
                                            req,
                                            cachedSmeTrans.getTranxType(),
                                            checkLimitTrans.getAmount(),
                                            checkLimitTrans.getCcy(),
                                            cachedSmeTrans.getMakerAuthenType(),
                                            isExecTrans);
                        }

                        if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                            log.error("Lỗi hạn mức");
                            return checkLimit;
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
                        try {
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
                        } catch (Exception e) {
                            log.info("Error: ", e);
                            transactionDetail.setResCode(Constants.ResCode.ERROR_96);
                            transactionDetail.setResDesc("Error");
                            transactionDetail.setTranxNote("Maker confirm failed");
                            transactionDetail.setDetail("Authenticate failed");
                            smeTransDetailRepository.save(transactionDetail);

                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        // Kiểm tra giao dịch đi ngay hay chờ duyệt
                        log.info("=====: " + cachedSmeTrans.getBranchCode());
                        // Xử lý lưu giao dich để check hạn mức
                        String type;
                        if (isExecTrans) {
                            resp = transactionService.execTransOutIBPS(resp,
                                    cachedSmeTrans,
                                    req);
                            if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                                transactionDetail.setResCode(resp.getCode());
                                transactionDetail.setTranxNote(resp.getMessage());
                                transactionDetail.setDetail(resp.getMessage());
                                smeTransDetailRepository.save(transactionDetail);
                                return resp;
                            }
                            smeTransDetailRepository.save(transactionDetail);
                            type = "2";
                        } else {
                            cachedSmeTrans.setStatus(Constants.TransStatus.MAKER_SUCCESS);
                            cachedSmeTrans.setTranxNote("Lập lệnh thành công");
                            type = "1";
                            smeTransRepository.save(cachedSmeTrans);
                            smeTransDetailRepository.save(transactionDetail);
                        }
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(cachedSmeTrans.getTranxType())) {
                            transactionLimitService.saveCheckTransLimit(
                                    user,
                                    req,
                                    cachedSmeTrans.getTranxType(),
                                    metadata.getCheckLimitTrans().getAmount(),
                                    metadata.getCheckLimitTrans().getCcy(),
                                    req.getAuthenType(),
                                    type, metadata.getFutureDate());
                        } else {
                            transactionLimitService.saveCheckTransLimit(
                                    user,
                                    req,
                                    cachedSmeTrans.getTranxType(),
                                    metadata.getCheckLimitTrans().getAmount(),
                                    metadata.getCheckLimitTrans().getCcy(),
                                    req.getAuthenType(),
                                    type);
                        }

                        cachedSmeTrans.setRequestProcessed(true);
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans); //update cache trans

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranxId(String.valueOf(cachedSmeTrans.getId()));
                        dataRp.setTranDate(CommonUtils.formatDate(new Date()));
                        dataRp.setIsExecTrans(isExecTrans ? "1" : "0");

                        // return transaction info
                        String metaStr = cachedSmeTrans.getMetadata();
                        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);
                        dataRp.setAmount(metaData.getOriginAmount());
                        dataRp.setExchangeAmount((long) metaData.getAmountVND());
                        dataRp.setClientRqCcy(metaData.getClientRqCcy());

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
                                        cachedSmeTrans.getToAcc(),
                                        null,
                                        null);
                        dataRp.setContact(isContact);

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

    public BaseClientResponse checkerInit(BaseCheckerInitRequest rq) {
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

                        // Check role type được quyền thao tác chức năng hay không
                        rp =
                                transactionService.validateUserAndServiceCode(
                                        rp,
                                        user,
                                        smeTrans.getTranxType(),
                                        "1",
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

                        TransactionMetaDataDTO metadata =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        // Kiểm tra hạn mức giao dịch chung
                        BaseClientResponse checkLimit =
                                transactionLimitService.checkTranLimit(
                                        user,
                                        rq,
                                        smeTrans.getTranxType(),
                                        metadata.getCheckLimitTrans().getAmount(),
                                        metadata.getCheckLimitTrans().getCcy(),
                                        checkAuthen.getAuthenMethod(),
                                        false);
                        if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                            log.error("Lỗi hạn mức");
                            return checkLimit;
                        }

                        // Khởi tạo ptxt
                        String tranToken = transactionService.genTranToken();
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTrans.getFromAcc(),
                                        smeTrans.getToAcc(),
                                        smeTrans.getId(),
                                        String.valueOf(smeTrans.getAmount()),
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
                        smeTrans.setCheckerAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans.setApprovedUser(user.getUsername());
                        smeTrans.setApprovedDate(LocalDateTime.now());
                        smeTrans.setApprovedMobile(user.getMobileOtp());
                        smeTrans = smeTransRepository.save(smeTrans);
                        smeTrans.setAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans.setChallenge(intAuthen.getDataAuthen());
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranToken(tranToken);

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

    public BaseClientResponse checkerConfirm(BaseConfirmRq req) {
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
                        SmeTrans cachedSmeTrans = gson.fromJson(redisCacheService.getTxn(req), SmeTrans.class);

                        //check block sync request
                        if (cachedSmeTrans.isRequestProcessed()) {
                            log.error("trans duplicate");
                            isDeleteCache = false;
                            String resCode = "028";
                            return new BaseClientResponse(
                                    resCode,
                                    commonService.getMessage("DUPL-028", req.getLang()));
                        }

                        if (!Constants.TransStatus.CHEKER_WAIT_CONFIRM.equals(cachedSmeTrans.getStatus())) {
                            log.info("Invalid trans status: {}", cachedSmeTrans.getStatus());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        if (!cachedSmeTrans.getCheckerAuthenType().equals(req.getAuthenType())) {
                            log.info("Invalid authenType: {}", req.getAuthenType());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        if (!user.getUsername().equals(cachedSmeTrans.getApprovedUser())) {
                            log.info("Init user ({}) and Confirm user ({}) are not the same", cachedSmeTrans.getApprovedUser(), user.getUsername());
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }

                        // Kiếm tra role type được quyền thực hiện chức năng hay không
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, cachedSmeTrans.getTranxType(), "2", req.getLang(), cachedSmeTrans.getCreatedUser());
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
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
                                        Strings.nullToEmpty(req.getChallenge()),
                                        String.valueOf(cachedSmeTrans.getAmount()),
                                        cachedSmeTrans.getServiceCode(),
                                        cachedSmeTrans.getCcy());
                        if (!Constants.ResCode.INFO_00.equals(initAuthenResponse.getCode())) {
                            resp.setCode(initAuthenResponse.getCode());
                            resp.setMessage(initAuthenResponse.getMessage());
                            return resp;
                        }

                        cachedSmeTrans.setRequestProcessed(true);
                        // Thực hiện lệnh chuyển khoản
                        switch (cachedSmeTrans.getServiceCode()) {
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE:
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED:
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO:
                                resp = transactionService.execTransOutIBPS(resp,
                                        cachedSmeTrans,
                                        req);
                                break;
                            case Constants.ServiceCode.FAST_TRANS_VIA_ACCNO:
                            case Constants.ServiceCode.FAST_TRANS_BILATERAL_ACCNO:
                                GetBankHostDateResponse hostDateResponse =
                                        coreQueryClient.getHostDate(new BaseBankRequest());
                                resp =
                                        transactionService.execTransfer247ViaAcc(
                                                resp, cachedSmeTrans, hostDateResponse.getCurrentDate(), req);
                                break;
                            case Constants.ServiceCode.FAST_TRANS_VIA_CARDNO:
                                hostDateResponse = coreQueryClient.getHostDate(new BaseBankRequest());
                                resp =
                                        transactionService.execTransfer247ViaCard(
                                                resp, cachedSmeTrans, hostDateResponse.getCurrentDate(), req);
                                break;
                            default:
                                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.ERROR_96);
                                resp.setMessage(
                                        commonService.getMessage(
                                                vn.vnpay.commoninterface.common.Constants.MessageCode.ERROR_96,
                                                req.getLang()));
                                break;
                        }


                        if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                            return resp;
                        }

                        // lưu chức năng gần đây
                        commonService.saveFuncRecent(cachedSmeTrans.getTranxType(),
                                user.getUsername(),
                                user.getRoleType(),
                                user.getConfirmType(),
                                req.getSource());

                        // Xử lý lưu giao dich để check hạn mức
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
                        // end
                        // TODO: Trả kết quả về cho client
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
        }
        finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse transConfirmInit(BaseCheckerInitRequest rq) {
        BaseClientResponse rp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            log.info("User info: " + gson.toJson(user));
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
                        // Kiểm tra số dư tại khoản debit với cache chuyển tiền ngay
                        if (!Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(smeTrans.getTranxType()) &&
                                !Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED.equals(smeTrans.getTranxType())) {
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
                        }

                        // check tk thụ hưởng
                        switch (smeTrans.getTranxType()) {
                            case Constants.ServiceCode.FAST_TRANS_VIA_ACCNO:
                                if (transactionMetaDataDTO.getCreditAccount() != null) {
                                    CheckBene247Request checkBene247Request = new CheckBene247Request();
                                    checkBene247Request.setToAcc(transactionMetaDataDTO.getCreditAccount().getAccountNo());
                                    checkBene247Request.setType("ACCOUNT");
                                    checkBene247Request.setBeneBankCode(smeTrans.getBeneBankCode());
                                    BaseClientResponse baseClientResponse = transfer247Service.checkBene247(checkBene247Request);
                                    if (!"00".equals(baseClientResponse.getCode())) {
                                        return baseClientResponse;
                                    }
                                }
                                break;
                            case Constants.ServiceCode.FAST_TRANS_VIA_CARDNO:
                                if (transactionMetaDataDTO.getCreditAccount() != null) {
                                    CheckBene247Request checkBene247Request = new CheckBene247Request();
                                    checkBene247Request.setCardToken(transactionMetaDataDTO.getCreditAccount().getAccountNo());
                                    checkBene247Request.setType("CARD");
                                    BaseClientResponse baseClientResponse = transfer247Service.checkBene247(checkBene247Request);
                                    if (!"00".equals(baseClientResponse.getCode())) {
                                        return baseClientResponse;
                                    }
                                }
                                break;
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
                        // Kiểm tra giao dịch đi thẳng
//                        boolean isExecTrans =
//                                transactionService.isExecTrans(
//                                        user.getRoleType(), user.getUsername(), smeTrans.getTranxType(), smeTrans.getFromAcc());
//                        log.info("isExecTrans? {}", isExecTrans);
                        // Kiểm tra hạn mức giao dịch chung
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
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

                        // Khởi tạo ptxt
                        String tranToken = transactionService.genTranToken();
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTrans.getFromAcc(),
                                        smeTrans.getToAcc(),
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
                        smeTrans.setApprovedDate(LocalDateTime.now());
                        smeTrans.setApprovedMobile(user.getMobileOtp());
                        smeTrans.setCheckerAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans = smeTransRepository.save(smeTrans);
                        smeTrans.setAuthenType(checkAuthen.getAuthenMethod());
                        smeTrans.setChallenge(intAuthen.getDataAuthen());
                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        BaseTransactionResponse dataRp = new BaseTransactionResponse();
                        dataRp.setTranToken(tranToken);

                        String authenType = checkAuthen.getAuthenMethod();
                        dataRp.setAuthenType(authenType);
                        dataRp.setDataAuthen(intAuthen.getDataAuthen());
                        dataRp.setFromAccount(smeTrans.getFromAcc());
                        dataRp.setToAccount(smeTrans.getToAcc());
                        dataRp.setAmount(transactionMetaDataDTO.getAmountVND());
                        log.info("Checker authen type: {}", authenType);

                        rp.setData(dataRp);

//                        smeTransRepository.updateTransStatusAndReasonAndApproveUser(
//                                Long.parseLong(rq.getTranxId()),
//                                Constants.TransStatus.CHEKER_WAIT_CONFIRM,
//                                rq.getReason(),
//                                rq.getUser());

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

    public BaseClientResponse transConfirm(BaseConfirmRq req) {
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
            // Lấy thông tin user
            SmeCustomerUser user = redisCacheService.getCustomerUser(req);

            SmeTrans smeTrans = smeTransRepository.findById(Long.parseLong(req.getTranxId())).get();
            log.info("trans from DB {}", gson.toJson(smeTrans));
            SmeTrans cachedSmeTrans = gson.fromJson(redisCacheService.getTxn(req), SmeTrans.class);
            log.info("trans from cache {}", gson.toJson(cachedSmeTrans));

            //check block sync request
            if (cachedSmeTrans.isRequestProcessed()) {
                log.error("trans duplicate");
                isDeleteCache = false;
                String resCode = "028";
                return new BaseClientResponse(
                        resCode,
                        commonService.getMessage("DUPL-028", req.getLang()));
            }

            // validate trans id
            if (!smeTrans.getId().equals(cachedSmeTrans.getId())) {
                log.error("trans id not match");
                resp.setCode(Constants.ResCode.ERROR_96);
                resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return resp;
            }

            if (!Constants.TransStatus.CHEKER_WAIT_CONFIRM.equals(cachedSmeTrans.getStatus())) {
                log.info("Invalid trans status: {}", cachedSmeTrans.getStatus());
                resp.setCode(Constants.ResCode.ERROR_96);
                resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return resp;
            }

            if (!cachedSmeTrans.getCheckerAuthenType().equals(req.getAuthenType())) {
                log.info("Invalid authenType: {}", req.getAuthenType());
                resp.setCode(Constants.ResCode.ERROR_96);
                resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return resp;
            }

            if (!user.getUsername().equals(cachedSmeTrans.getApprovedUser())) {
                log.info("Init user ({}) and Confirm user ({}) are not the same", cachedSmeTrans.getApprovedUser(), user.getUsername());
                resp.setCode(Constants.ResCode.ERROR_96);
                resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                return resp;
            }


            if (smeTrans == null
                    || (!"5".equals(smeTrans.getStatus()) && !"10".equals(smeTrans.getStatus()))) {
                return new BaseClientResponse(
                        Constants.ResCode.ERROR_0204,
                        commonService.getMessage(Constants.MessageCode.CONFIRM_TRANS_FAIL, req.getLang()));
            }

            if (!user.getCif().equals(smeTrans.getCifNo())) {
                return new BaseClientResponse(
                        Constants.ResCode.ERROR_96,
                        commonService.getMessage(Constants.MessageCode.INVALID_DATA, req.getLang()));
            }

            if (user != null) {
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Valid giao dich
                        BaseClientResponse valid = transactionService.validTxn(req);
                        if (!Constants.ResCode.INFO_00.equals(valid.getCode())) {
                            return valid;
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
                        // Xử lý lưu giao dich để check hạn mức
                        TransactionMetaDataDTO metadata =
                                gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);

                        String beneAcc = smeTrans.getToAcc();

                        // Thực hiện lệnh chuyển khoản
                        switch (cachedSmeTrans.getTranxType()) {
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE:
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED:
                            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO:
                                cachedSmeTrans.setApprovedUser(req.getUser());
                                resp = transactionService.execTransOutIBPS(resp,
                                        cachedSmeTrans,
                                        req);
                                break;
                            case Constants.ServiceCode.FAST_TRANS_VIA_ACCNO:
                            case Constants.ServiceCode.FAST_TRANS_BILATERAL_ACCNO:
                                GetBankHostDateResponse hostDateResponse =
                                        coreQueryClient.getHostDate(new BaseBankRequest());
                                resp =
                                        transactionService.execTransfer247ViaAcc(
                                                resp, cachedSmeTrans, hostDateResponse.getCurrentDate(), req);
                                break;
                            case Constants.ServiceCode.FAST_TRANS_VIA_CARDNO:
                                beneAcc = metadata.getCreditAccount().getAccountNo();
                                hostDateResponse = coreQueryClient.getHostDate(new BaseBankRequest());
                                resp =
                                        transactionService.execTransfer247ViaCard(
                                                resp, cachedSmeTrans, hostDateResponse.getCurrentDate(), req);
                                break;
                            case Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE:
                            case Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED:
                                // Đăng ký giao dịch tương lai/định kỳ sang bank
                                metadata.getFutureTransData().setTransId(String.valueOf(smeTrans.getId()));
                                metadata.getFutureTransData().setBatchId(String.valueOf(smeTrans.getId()));
                                metadata.getFutureTransData().setCheckerId(req.getUser());
                                smeTrans.setMetadata(gson.toJson(metadata));

                                RegisterFutureTransBankRequest bankReq =
                                        RegisterFutureTransBankRequest.builder()
                                                .transData(metadata.getFutureTransData())
                                                .build();
                                RegisterFutureTransBankResponse bankResp = miscClient.registerFutureTrans(bankReq);
                                smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
                                smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());
                                if (bankResp.getResponseStatus().getIsSuccess()) {
                                    smeTrans.setStatus(Constants.TransStatus.MAKER_SUCCESS);
                                    smeTrans.setTranxNote("Duyệt lệnh thành công");
                                } else {
                                    // Cập nhật trạng thái giao dịch
                                    if (bankResp.getResponseStatus().getIsTimeout()) {
                                        smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                                        smeTrans.setTranxNote("Giao dịch timed out");
                                    } else {
                                        smeTrans.setStatus(Constants.TransStatus.FAIL);
                                        smeTrans.setTranxNote("Giao dịch lỗi");
                                    }

                                    smeTransRepository.save(smeTrans);

                                    resp.setCode(bankResp.getResponseStatus().getResCode());
                                    resp.setMessage(bankResp.getResponseStatus().getResMessage());
                                }
                                break;
                            default:
                                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.ERROR_96);
                                resp.setMessage(
                                        commonService.getMessage(
                                                vn.vnpay.commoninterface.common.Constants.MessageCode.ERROR_96,
                                                req.getLang()));
                                break;
                        }

                        // Gửi email sao kê
                        if (Constants.EmailReceiveConfig.RECEIVE_ALL.equals(user.getRegistBillReceive())
                                || Constants.EmailReceiveConfig.RECEIVE_FINANCE.equals(user.getRegistBillReceive())) {
                            String emailType = "TRANSFER_IMMEDIATE";
                            String emailReceiver = user.getEmail();
                            metadata.setOrgTran(cachedSmeTrans);
                            log.info("emailData: " + gson.toJson(metadata));
                            smeApiServiceClient.sendEmailCommon(SendEmailCommonRequest.builder()
                                    .emailCode(emailType)
                                    .emailReceiver(emailReceiver)
                                    .obj(metadata)
                                    .build());

                            // send to maker
                            if (!Strings.isNullOrEmpty(metadata.getMakerEmail())) {
                                emailReceiver = metadata.getMakerEmail();
                                smeApiServiceClient.sendEmailCommon(SendEmailCommonRequest.builder()
                                        .emailCode(emailType)
                                        .emailReceiver(emailReceiver)
                                        .obj(metadata)
                                        .build());
                            }
                        }

                        // end

                        log.info(gson.toJson(resp));
                        TransactionMetaDataDTO transactionMetaDataDTO =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        TransConfirmRes transConfirmRes =
                                TransConfirmRes.builder()
                                        .tranxId(String.valueOf(cachedSmeTrans.getId()))
                                        .tranxDate(CommonUtils.getDate("HH:mm dd/MM/yyyy"))
                                        .fee(transactionMetaDataDTO.getFee().getOriginAmount())
                                        .exchangeFee(transactionMetaDataDTO.getFee().getAmount())
                                        .vat(transactionMetaDataDTO.getFee().getOriginVatAmount())
                                        .exchangeVat(transactionMetaDataDTO.getFee().getVatAmount())
                                        .totalFee(
                                                transactionMetaDataDTO.getFee().getOriginAmount()
                                                        + transactionMetaDataDTO.getFee().getOriginVatAmount())
                                        .exchangeTotalFee(
                                                transactionMetaDataDTO.getFee().getAmount()
                                                        + transactionMetaDataDTO.getFee().getVatAmount())
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
                                                        smeTrans.getTranxType(),
                                                        smeTrans.getBeneBankCode(),
                                                        req.getUser(),
                                                        null,
                                                        beneAcc,
                                                        null,
                                                        null))
                                        .build();
                        resp.setData(transConfirmRes);

                        if ("00".equals(resp.getCode()) || "0".equals(resp.getCode())) {
                            transactionDetail.setResCode(resp.getCode());
                            transactionDetail.setResDesc(resp.getMessage());
                            smeTransDetailRepository.save(transactionDetail);

                            if (cachedSmeTrans.getTranxType().equals(Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE)
                                    || cachedSmeTrans.getTranxType().equals(Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE)) {
                                transactionLimitService.saveCheckTransLimit(
                                        user,
                                        req,
                                        cachedSmeTrans.getTranxType(),
                                        metadata.getCheckLimitTrans().getAmount(),
                                        metadata.getCheckLimitTrans().getCcy(),
                                        req.getAuthenType(),
                                        "2",
                                        metadata.getFutureDate());
                            } else {
                                transactionLimitService.saveCheckTransLimit(
                                        user,
                                        req,
                                        cachedSmeTrans.getTranxType(),
                                        metadata.getCheckLimitTrans().getAmount(),
                                        metadata.getCheckLimitTrans().getCcy(),
                                        req.getAuthenType(),
                                        "2");
                            }

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
        }
        finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse initTransBatchConfirm(InitConfirmTransBatchRequest rq) {
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
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTranss.get(0).getFromAcc(),
                                        smeTranss.get(0).getToAcc(),
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

    public BaseClientResponse transBatchConfirm(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        boolean isDeleteCache = true;
        try {
            // Kiểm tra giao dịch có đang xử lý
            boolean isExe = !cacheService.setnx(req.getTranxId(), 15l);
            if (isExe) {
                isDeleteCache = false;
                String resCode = "028";
                return new BaseClientResponse(
                        resCode,
                        commonService.getMessage("DUPL-028", req.getLang()));
            }
            // Lấy thông tin user
            SmeCustomerUser user = cacheService.getCustomerUser(req);
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
                        cacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);

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
                cacheService.delete(req.getTranxId());
        }
        return resp;
    }
}
