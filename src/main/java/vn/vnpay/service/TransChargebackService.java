package vn.vnpay.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.feignclient.VCBServiceGWClient;
import vn.vnpay.commoninterface.request.BaseCheckerInitRequest;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.AuthenMethodResponse;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.response.BaseTransactionResponse;
import vn.vnpay.commoninterface.response.InitAuthenResponse;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.commoninterface.service.RedisCacheService;
import vn.vnpay.commoninterface.service.TransactionLimitService;
import vn.vnpay.commoninterface.service.TransactionService;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.entity.*;
import vn.vnpay.dbinterface.entitydboffline.SmeTransOffline;
import vn.vnpay.dbinterface.repository.*;
import vn.vnpay.dbinterface.reposotorydboffline.SmeTransRepositoryOffline;
import vn.vnpay.dto.SmeCheckerServiceRoleDTO;
import vn.vnpay.dto.SmeMakerServiceRoleDTO;
import vn.vnpay.request.*;
import vn.vnpay.response.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TransChargebackService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    private Gson gson;

    @Autowired
    private VCBServiceGWClient serviceGWClient;

    @Autowired
    private CoreQueryClient coreQueryClient;

    @Autowired
    private MbServiceRepository mbServiceRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionLimitService transactionLimitService;

    @Autowired
    private SmeTransDetailRepository smeTransDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SmeMakerServiceRoleRepository smeMakerServiceRoleRepository;

    @Autowired
    private SmeCheckerServiceRoleRepository smeCheckerServiceRoleRepository;

    @Autowired
    private SmeCustomerUserRepository smeCustomerUserRepository;

    @Autowired
    private SmeUserAccRoleRepository smeUserAccRoleRepository;

    @Autowired
    private SmeTransRepositoryOffline smeTransRepositoryOffline;

    public BaseClientResponse makerInitTransChargeback(TransChargebackRequest rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        BaseTransactionResponse dataRp = new BaseTransactionResponse();
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                // Kiểm tra trạng thái của user
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        // Kiểm tra tài khoản debit
                        List<AccountDTO> listAccount = user.getListAccount();
                        Optional<AccountDTO> optDebit =
                                listAccount.stream()
                                        .filter(
                                                x ->
                                                        (rq.getFromAcc().equalsIgnoreCase(x.getAccountNo())
                                                                || rq.getFromAcc().equalsIgnoreCase(x.getAccountAlias())))
                                        .findFirst();

                        if (!optDebit.isPresent()) {
                            resp.setCode(Constants.ResCode.INFO_23);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, rq.getLang()));
                            return resp;
                        }
                        AccountDTO fromAcc = optDebit.get();

                        String cacheTrans = redisCacheService
                                .get("detail_trans_chargeback" + user.getUsername() + rq.getSessionId());
                        log.info("Cache old trans: {}", cacheTrans);
                        if (cacheTrans == "" || cacheTrans == null) {
                            log.info("Data old trans from cache not found");
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return resp;
                        }
                        SmeTrans oldTrans = gson.fromJson(cacheTrans, SmeTrans.class);
                        TransactionMetaDataDTO metadataOldTrans = gson.fromJson(oldTrans.getMetadata(), TransactionMetaDataDTO.class);

                        //kiểm tra user chỉ được quyền lập tra soát với giao dịch mình tạo
                        if ((Constants.UserRole.MAKER.equals(user.getRoleType()) || Constants.UserRole.ALL.equals(user.getRoleType())) && !rq.getUser().equals(oldTrans.getCreatedUser())) {
                            log.info("maker not role creat trans charge with trans");
                            resp.setCode(Constants.ResCode.INFO_77);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_77, rq.getLang()));
                            return resp;
                        }
                        //kiểm tra xem giao dịch đã lập lệnh tra soát chưa
                        if ("0".equals(metadataOldTrans.getStatusCreateChargeback())) {
                            log.info("giao dịch đã được khởi tạo tra soát");
                            resp.setCode(Constants.ResCode.INFO_78);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_78, rq.getLang()));
                            return resp;
                        }

                        // Khởi tạo thông tin metadata
                        TransactionMetaDataDTO metadata = TransactionMetaDataDTO.builder().build();

                        // Init transaction remark
                        StringBuilder remark = new StringBuilder();
                        if (Constants.SOURCE_IB.equals(rq.getSource())) {
                            remark.append("IBBIZ.$1.");
                        } else {
                            remark.append("MBBIZ.$1.");
                        }

                        // Lấy thông tin chi tiết tài khoản debit
                        log.info("Get Debit account details for {}", rq.getFromAcc());
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(rq.getFromAcc());
                        accDetailsReq.setAccountType(fromAcc.getAccountType());
                        accDetailsReq.setAlias(rq.getFromAcc().equalsIgnoreCase(fromAcc.getAccountAlias()));
                        AccountDetailBankResponse debitBankResp =
                                coreQueryClient.getDDAccountDetails(accDetailsReq);
                        if (!"0".equals(debitBankResp.getResponseStatus().getResCode())) {
                            log.info("Failed to get debit account details");
                            String code = "0199";
                            if (debitBankResp.getResponseStatus().getIsFail()) {
                                code = debitBankResp.getResponseStatus().getResCode();
                            }
                            resp.setCode(code);
                            resp.setMessage(commonService.getMessage("ACC-DETAIL-" + code, rq.getLang()));
                            return resp;
                        }
                        // Kiểm tra trạng thái tài khoản debit
                        if (!Constants.ALLOWED_ACC_STT_DEBIT.contains(debitBankResp.getAccountStatus())) {
                            log.info("Invalid debit account status: {}", debitBankResp.getAccountStatus());
                            resp.setCode(Constants.ResCode.INFO_37);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_37, rq.getLang()));
                            return resp;
                        }

                        metadata.setDebitName(debitBankResp.getAccountName());
                        metadata.setDebitAddr(debitBankResp.getAccountAddress());
                        String serviceCode = Constants.ServiceCode.TRANS_CHARGEBACK;
                        // Lấy thông tin service type
                        Optional<MbService> mbServiceOpt = mbServiceRepository.findByServiceCode(serviceCode);
                        if (!mbServiceOpt.isPresent()) {
                            log.info("Invalid service code: {}", serviceCode);
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return resp;
                        }

                        // Kiểm tra quyền giao dịch tài khoản nguồn
                        resp = transactionService.validateTransAuthorityForAccount(resp, user, rq.getFromAcc(), serviceCode, rq.getLang());
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Kiếm tra role type được quyền thực hiện chức năng với giao dịch cũ cần tra soát hay không
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, serviceCode, "1", rq.getLang(), null);
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Kiểm tra mã sản phẩm tài khoản
                        resp =
                                transactionService.validateAccountProduct(
                                        resp,
                                        serviceCode,
                                        debitBankResp.getProductCode(),
                                        null,
                                        rq.getLang());
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Kiểm tra trạng thái tài khoản debit
                        resp =
                                transactionService.checkDebitAccountStatus(
                                        resp,
                                        rq.getFromAcc(),
                                        fromAcc.getAccountType(),
                                        rq.getFromAcc().equalsIgnoreCase(fromAcc.getAccountAlias()),
                                        true,
                                        rq.getLang());
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return resp;
                        }

                        // Lấy phương thức xác thực
                        AuthenMethodResponse checkAuthen = transactionService.getAuthenMethod(user);
                        if (!Constants.MessageCode.INFO_00.equals(checkAuthen.getCode())) {
                            resp.setCode(checkAuthen.getCode());
                            resp.setMessage(commonService.getMessage(checkAuthen.getCode(), rq.getLang()));
                            return resp;
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
                                resp.setCode(bankResp.getResponseStatus().getResCode());
                                resp.setMessage(bankResp.getResponseStatus().getResMessage());
                                return resp;
                            }
                            rate = bankResp.getAppXferBuy();
                            if ("USD".equals(debitBankResp.getCurCode())) {
                                rateUSD = rate;
                            } else {
                                bankResp =
                                        coreQueryClient.getExchangeRateInquiry(
                                                ExchangeRateInquiryBankRequest.builder().currency("USD").build());
                                if (bankResp.getResponseStatus().getIsFail()) {
                                    resp.setCode(bankResp.getResponseStatus().getResCode());
                                    resp.setMessage(
                                            commonService.getMessage(
                                                    bankResp.getResponseStatus().getResCode(), rq.getLang()));
                                    return resp;
                                }
                                rateUSD = bankResp.getAppXferBuy();
                            }
                        }

                        // Quy đổi ngoại tệ <-> VND
                        BigDecimal amountVND, originAmount;
                        amountVND = new BigDecimal(rq.getFeeTSOL()).setScale(0, RoundingMode.HALF_UP);
                        originAmount = amountVND.divide(rate, 2, RoundingMode.HALF_UP);
                        // Tính phí giao dịch
                        String vatExamptFlag = commonService.getVatExemptFlag(rq);
                        BigDecimal feeAmt, feeVat, originFeeAmt, originFeeVat, feeU, vatU;
                        feeAmt = feeVat = originFeeAmt = originFeeVat = feeU = vatU = BigDecimal.ZERO;
                        // Bổ sung thông tin vào metadata
                        metadata.setTotalFee(originFeeAmt.doubleValue() + originFeeVat.doubleValue());
                        metadata.setExchangeTotalFee(feeAmt.doubleValue() + feeVat.doubleValue());
                        metadata.setAmountVND(amountVND.longValue());
                        metadata.setOriginAmount(originAmount.doubleValue());
                        metadata.setFeeU(feeU);
                        metadata.setVatU(vatU);
                        metadata.setVatExamptFlag(vatExamptFlag);

                        // Kiểm tra giao dịch đi thẳng
                        boolean isExecTrans =
                                transactionService.isExecTrans(
                                        user.getRoleType(), user.getUsername(), serviceCode, rq.getFromAcc());
                        log.info("isExecTrans? {}", isExecTrans);

                        // Kiểm tra số dư tài khoản debit
                        String minBal =
                                "VND".equals(debitBankResp.getCurCode())
                                        ? commonService.getConfig("MIN_BALANCE", "50000")
                                        : commonService.getConfig("MIN_BALANCE_FOR", "10");
                        log.info("minBal: {}", minBal);
                        if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                .subtract(new BigDecimal(minBal))
                                .doubleValue()
                                < rq.getFeeTSOL()) {
                            if (isExecTrans) {
                                resp.setCode(Constants.ResCode.ERROR_112);
                                resp.setMessage(
                                        commonService.getMessage(Constants.MessageCode.ERROR_112, rq.getLang()));
                                return resp;
                            } else {
                                if (!"1".equals(rq.getIsByPassNotBalance())) {
                                    resp.setCode(Constants.ResCode.INFO_45);
                                    resp.setMessage(
                                            commonService.getMessage(Constants.MessageCode.INFO_45, rq.getLang()));
                                    return resp;
                                }
                            }
                        }

                        // Convert tiền tệ khác USD, và VND để tính toàn hạn mức
                        BigDecimal originAmountOld = new BigDecimal(metadataOldTrans.getOriginAmount());
                        BigDecimal amountVNDOld = new BigDecimal(metadataOldTrans.getAmountVND());
                        String ccyCheckLimit = oldTrans.getCcy();
                        if (metadataOldTrans.getDebitAccount() != null) {
                            ccyCheckLimit = metadataOldTrans.getDebitAccount().getCurrency();
                        }
                        CheckLimitTrans checkLimitTrans =
                                transactionLimitService.convertCcyAmount(
                                        resp, ccyCheckLimit, originAmountOld, amountVNDOld);
                        if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                            return resp;
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

                        long transId = smeTransRepository.getNextValSmeTransSeq().longValue();
                        //build remark
                        remark.append(" Thu phi tra soat truc tuyen tu dong tu ")
                                .append(rq.getFromAcc())
                                .append(" ")
                                .append(debitBankResp.getAccountName());
                        String remarkStr = remark.toString();
                        remarkStr = remarkStr.replace("$1", String.valueOf(transId));
                        log.info("Remark: {}", remarkStr);
                        // Build Trans object
                        SmeTrans smeTrans = new SmeTrans();
                        smeTrans.setId(transId);
                        smeTrans.setTranxContent(rq.getContent());
                        smeTrans.setMakerAuthenType(authenType);
                        smeTrans.setCusName(user.getCusName());
                        smeTrans.setCreatedUser(user.getUsername());
                        smeTrans.setCreatedMobile(user.getMobileOtp());
                        smeTrans.setFromAcc(rq.getFromAcc());
//                    smeTrans.setToAcc(rq.getToAcc());
                        smeTrans.setTranxType(serviceCode);
                        smeTrans.setTranxNote("Maker init");
                        smeTrans.setCifNo(user.getCif());
                        smeTrans.setTranxTime(LocalDateTime.now());
                        smeTrans.setServiceType(mbServiceOpt.get().getServiceType());
                        smeTrans.setCcy(fromAcc.getCurCode());
                        smeTrans.setStatus(Constants.TransStatus.MAKER_WAIT_CONFIRM);
                        smeTrans.setFeeType("1");
//                    smeTrans.setCreditName(realAccName != null? realAccName : creditBankResp.getAccountName());
                        smeTrans.setBeneBankCode("970436");
//                    smeTrans.setBeneBranchCode(creditBankResp.getBranchNo());
                        smeTrans.setBranchCode(user.getBranchCode());
                        smeTrans.setChannel(rq.getSource());
                        smeTrans.setFeeOnAmt(feeVat.doubleValue());
                        smeTrans.setFlatFee(feeAmt.doubleValue());
                        smeTrans.setAmount(originAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());
                        smeTrans.setTotalAmount(amountVND.longValue());
                        smeTrans.setDebitBranchCode(debitBankResp.getBranchNo());
                        smeTrans.setCifInt(user.getCifInt());
                        smeTrans.setRealAmount(amountVND);

                        FeeDTO fee =
                                FeeDTO.builder()
                                        .amount(feeAmt.doubleValue())
                                        .authMethod(0)
                                        .currency(fromAcc.getCurCode())
                                        .originAmount(originFeeAmt.doubleValue())
                                        .originAuthMethod(0.0)
                                        .originVatAmount(originFeeVat.doubleValue())
                                        .type(Integer.parseInt("1"))
                                        .vatAmount(feeVat.doubleValue())
                                        .build(); // Phí

                        // lấy response detail bank from cache
                        String cacheDetailTrans = redisCacheService
                                .get("response_detail_trans_chargeback_" + user.getUsername() + rq.getSessionId());
                        log.info("Cache response trans: {}", cacheDetailTrans);
                        if (cacheDetailTrans == "" || cacheDetailTrans == null) {
                            log.info("Data detail trans from cache not found");
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return resp;
                        }
                        GetTransChargebackInfoBankResponse transDetailResponseBankCache = gson.fromJson(cacheDetailTrans, GetTransChargebackInfoBankResponse.class);
                        TransDetailChargebackDTO transDetailCache = transDetailResponseBankCache.getTransDetail();
                        TransDetailChargebackDTO tsolTransaction = TransDetailChargebackDTO.builder().build();
                        boolean checkTsolTrans = false;
                        //kiểm tra xem có thay đổi giá trị nào đẩy bank không
                        if (StringUtils.isNotBlank(rq.getCreditAcc()) || StringUtils.isNotBlank(rq.getCreditName())
                                || StringUtils.isNotBlank(rq.getContent()) || StringUtils.isNotBlank(rq.getIdNo())
                                || StringUtils.isNotBlank(rq.getIssueDate()) || StringUtils.isNotBlank(rq.getIssuePlace())) {
                            checkTsolTrans = true;
                            tsolTransaction.setCreditaccount(StringUtils.isNotBlank(rq.getCreditAcc()) ? rq.getCreditAcc() : null);
                            tsolTransaction.setCreditname(StringUtils.isNotBlank(rq.getCreditName()) ? rq.getCreditName() : null);
                            tsolTransaction.setRemark(StringUtils.isNotBlank(rq.getContent()) ? rq.getContent() : null);
                            tsolTransaction.setIdno(StringUtils.isNotBlank(rq.getIdNo()) ? rq.getIdNo() : null);
                            tsolTransaction.setIssuE_DATE(StringUtils.isNotBlank(rq.getIssueDate()) ? rq.getIssueDate() : null);
                            tsolTransaction.setIssuE_PLACE(StringUtils.isNotBlank(rq.getIssuePlace()) ? rq.getIssuePlace() : null);
                        }
                        //build amount to feeDataObject
                        String glAcc;
                        double feeAmtOrigin;
                        double feeAmtVND;
                        double feeFlatVND;
                        double feeAmtVatOrigin;
                        double feeAmtVatVND;
                        //kiểm tra xem cif có đc miễn VAT không
                        if ("Y".equalsIgnoreCase(vatExamptFlag)) {
                            glAcc = commonService.getConfig("GL_TSOL_NO_VAT", "");
                            BigDecimal feeTSOL = new BigDecimal(rq.getFeeTSOL());   //vnd
                            if ("VND".equals(fromAcc.getCurCode())) {
                                feeAmtOrigin = feeAmtVND = feeTSOL.doubleValue();
                                feeFlatVND = feeTSOL.doubleValue();
                                feeAmtVatOrigin = 0.0;
                                feeAmtVatVND = 0.0;
                            } else {
                                feeAmtVND = feeTSOL.doubleValue();
                                feeFlatVND = feeTSOL.doubleValue();
                                feeAmtOrigin = feeTSOL.divide(rate, 2, RoundingMode.HALF_UP).doubleValue();
                                feeAmtVatOrigin = 0.0;
                                feeAmtVatVND = 0.0;
                            }
                        } else {
                            glAcc = commonService.getConfig("GL_TSOL", "");
                            String tsolFeeDiscountStr = commonService.getConfig("TSOL_FEE_DISCOUNT", "0");
                            double tsolFeeDiscountDouble = Double.parseDouble(tsolFeeDiscountStr) / 100;
                            BigDecimal totalFeeDiscount = new BigDecimal(1 + tsolFeeDiscountDouble);
                            BigDecimal feeTSOL = new BigDecimal(rq.getFeeTSOL());   //vnd
                            double feeamT_FLAT_VND = feeTSOL.divide(totalFeeDiscount, 0, RoundingMode.HALF_UP).doubleValue(); //vnd
                            double feeamT_VAT_OGRIN = rq.getFeeTSOL() - feeamT_FLAT_VND;    //vnd
                            double feeamT_VAT_VND = feeamT_VAT_OGRIN;   //vnd
                            if ("VND".equals(fromAcc.getCurCode())) {
                                feeAmtOrigin = feeAmtVND = feeTSOL.doubleValue();
                                feeFlatVND = feeamT_FLAT_VND;
                                feeAmtVatOrigin = feeamT_VAT_OGRIN;
                                feeAmtVatVND = feeamT_VAT_VND;
                            } else {
                                feeAmtVND = feeTSOL.doubleValue();
                                feeAmtOrigin = feeTSOL.divide(rate, 2, RoundingMode.HALF_UP).doubleValue();
                                feeFlatVND = feeamT_FLAT_VND;
                                feeAmtVatOrigin = (new BigDecimal(feeamT_VAT_OGRIN)).divide(rate, 2, RoundingMode.HALF_UP).doubleValue();
                                feeAmtVatVND = feeamT_VAT_VND;
                            }
                        }

                        FeeTransDetailChargebackDTO feeDataObject = FeeTransDetailChargebackDTO.builder()
                                .accountno(rq.getFromAcc())
                                .accountcurrency(fromAcc.getCurCode())
                                .glaccount(glAcc)
                                .feeamountogrin(feeAmtOrigin)
                                .feeamountvnd(feeAmtVND)
                                .feeamT_FLAT_VND(feeFlatVND)
                                .feeamT_VAT_OGRIN(feeAmtVatOrigin)
                                .feeamT_VAT_VND(feeAmtVatVND)
                                .remark(remarkStr)
                                .build();
                        //buid request tạo lệnh tra soát
                        CreateTransChargebackBankRequest createTransChargebackBankRequest =
                                CreateTransChargebackBankRequest.builder()
                                        .serviceCode(transDetailResponseBankCache.getServiceCode())
                                        .requestTSID(rq.getRequestTSID())
                                        .reasonID(rq.getReasonID())
                                        .departmentID(rq.getDepartmentID())
                                        .teller(rq.getTeller())
                                        .sequence(rq.getSequence())
                                        .cif(transDetailCache.getCif())
                                        .hostDate(rq.getHostdate())
                                        .remark(oldTrans.getTranxRemark())
                                        .cusCode(user.getUsername())
                                        .amount(transDetailCache.getAmount())
                                        .currency(transDetailCache.getCurrency())
                                        .cusAcct(transDetailCache.getDebitaccount())
                                        .cusFullName(transDetailCache.getDebitname())
                                        .pcTime(rq.getPcTime())
                                        .type_request(0)
                                        .is_auto_fee(transDetailCache.getIS_AUTO_FEE())
                                        .feeDataObject(feeDataObject)
                                        .build();
                        if (checkTsolTrans)
                            createTransChargebackBankRequest.setTsolTransaction(tsolTransaction);
                        //lay thong tin serviceCode của giao dịch được tra soát
                        Optional<MbService> mbServiceOldOpt = mbServiceRepository.findByServiceCode(oldTrans.getTranxType());
                        String serviceCodeOld = "";
                        String servideNameOld = "";
                        if (mbServiceOldOpt.isPresent()) {
                            serviceCodeOld = mbServiceOldOpt.get().getServiceCode();
                            servideNameOld = mbServiceOldOpt.get().getServiceName();
                        }
                        //lưu thêm phí giao dịch tra soát
                        metadata.setFeeTSOLOrigin(feeAmtOrigin);
                        metadata.setFeeTSOLVND(feeAmtVND);
                        double feeAmtOriginSave = (new BigDecimal(feeFlatVND)).divide(rate, 2, RoundingMode.HALF_UP).doubleValue();
                        metadata.setFeeTSOLAmtOrigin(feeAmtOriginSave);
                        metadata.setFeeTSOLAmtVND(feeFlatVND);
                        metadata.setVatTSOLAmtOrigin(feeAmtVatOrigin);
                        metadata.setVatTSOLAmtVND(feeAmtVatVND);

                        metadata.setFee(fee);
                        metadata.setRequestTSID(rq.getRequestTSID());
                        metadata.setRequestTSName(rq.getRequestTSName());
                        metadata.setRequestTSNameEN(rq.getRequestTSNameEN());
                        metadata.setAmountTrans(oldTrans.getAmount());
                        metadata.setCcyTrans(oldTrans.getCcy());
                        metadata.setServiceCodeTrans(serviceCodeOld);
                        metadata.setServiceCodeTransName(servideNameOld);
                        metadata.setReasonID(rq.getReasonID());
                        metadata.setReasonName(rq.getReasonName());
                        metadata.setReasonNameEN(rq.getReasonNameEN());
                        metadata.setFeeTSOL(rq.getFeeTSOL());
                        metadata.setIdOld(oldTrans.getId());
                        metadata.setRemarkOld(oldTrans.getTranxRemark()
                                .replace("$1", String.valueOf(oldTrans.getId()))
                                .replace("$2", String.valueOf(metadataOldTrans.getSequence())));
                        metadata.setCreatedDateOld(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(oldTrans.getTranxTime()));
                        metadata.setCreditNameOld(metadataOldTrans.getCreditAccount().getAccountHolderName());
                        metadata.setCreditBankNameOld(metadataOldTrans.getCreditAccount().getBankName());
                        metadata.setCreditAccOld(metadataOldTrans.getCreditAccount().getAccountNo());
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(oldTrans.getTranxType()) && StringUtils.isNotBlank(metadataOldTrans.getCardMaskingNumber())) {
                            metadata.setCreditAccOld(metadataOldTrans.getCardMaskingNumber());
                        }
                        if (metadataOldTrans.getRecipient() != null) {
                            metadata.setIdNoOld(metadataOldTrans.getRecipient().getId());
                            metadata.setIssueDateOld(CommonUtils.TimeUtils.format("yyyy-MM-dd", "dd/MM/yyyy", metadataOldTrans.getRecipient().getIssuedDate()));
                            metadata.setIssuePlaceOld(metadataOldTrans.getRecipient().getIssuedPlace());
                        }
                        metadata.setCreateTransChargebackBankRequest(createTransChargebackBankRequest);
                        smeTrans.setMetadata(gson.toJson(metadata));
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
                                            rq.getFromAcc(),
                                            smeTrans.getId(),
                                            (rq.getFeeTSOL()) + "",
                                            tranToken,
                                            serviceCode,
                                            "",
                                            "VND");
                            if (!Constants.ResCode.INFO_00.equals(intAuthen.getCode())) {
                                log.error("init authen error: " + intAuthen.getCode());

                                transactionDetail.setResCode(intAuthen.getCode());
                                transactionDetail.setResDesc(intAuthen.getDataAuthen());
                                transactionDetail.setTranxNote("Maker init failed");
                                transactionDetail.setDetail("Init authen method failed");
                                smeTransDetailRepository.save(transactionDetail);

                                resp.setCode(intAuthen.getCode());
                                resp.setMessage(intAuthen.getMessage());
                                return resp;
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

                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                            return resp;
                        }

                        smeTransDetailRepository.save(transactionDetail);

                        // Lưu cache giao dich
                        redisCacheService.pushTxn(rq, tranToken, smeTrans);

                        // Trả ra dữ liệu cho client
                        dataRp.setTranxId(String.valueOf(smeTrans.getId()));
                        dataRp.setTsolRef(String.valueOf(smeTrans.getId()));
                        resp.setData(dataRp);
                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public BaseClientResponse makerConfirmTransChargeback(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        boolean isDeleteCache = true;
        try {
            BaseTransactionResponse dataRp = new BaseTransactionResponse();
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
                        // Get cache value
                        SmeTrans cachedSmeTrans = gson.fromJson(redisCacheService.getTxn(req), SmeTrans.class);

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

                        String cacheTransOld = redisCacheService
                                .get("detail_trans_chargeback" + user.getUsername() + req.getSessionId());
                        log.info("Cache old trans: {}", cacheTransOld);
                        if (cacheTransOld == "" || cacheTransOld == null) {
                            log.info("Data old trans from cache not found");
                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }
                        SmeTrans oldTrans = gson.fromJson(cacheTransOld, SmeTrans.class);

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

                        TransactionMetaDataDTO metadata = gson.fromJson(cachedSmeTrans.getMetadata(), TransactionMetaDataDTO.class);

                        BaseClientResponse checkLimit;
                        String serviceCode = cachedSmeTrans.getTranxType();

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

                            resp.setCode(Constants.ResCode.ERROR_96);
                            resp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
                            return resp;
                        }
                        smeTransDetailRepository.save(transactionDetail);

                        CheckLimitTrans checkLimitTrans = metadata.getCheckLimitTrans();
                        checkLimit =
                                transactionLimitService.checkTranLimit(
                                        user,
                                        req,
                                        serviceCode,
                                        checkLimitTrans.getAmount(),
                                        checkLimitTrans.getCcy(),
                                        req.getAuthenType(),
                                        metadata.isExecTrans());

                        if (!Constants.ResCode.INFO_00.equals(checkLimit.getCode())) {
                            log.error("Lỗi hạn mức");
                            return checkLimit;
                        }

                        // Kiểm tra giao dịch đi ngay hay chờ duyệt
                        dataRp.setIsExecTrans(metadata.isExecTrans() ? "1" : "0");
                        log.info("=====: " + cachedSmeTrans.getBranchCode());
                        // Xử lý lưu giao dich để check hạn mức
                        String type;
                        if (metadata.isExecTrans()) {
                            resp = transactionService.execTransChargeback(req, resp, cachedSmeTrans);
                            if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                                transactionDetail.setResCode(resp.getCode());
                                transactionDetail.setTranxNote(resp.getMessage());
                                transactionDetail.setDetail(resp.getMessage());
                                smeTransDetailRepository.save(transactionDetail);
                                return resp;
                            }
                            smeTransDetailRepository.save(transactionDetail);
                            type = "2";
                            //lưu trạng thái để khóa giao dịch đã tra soát
                            updateStatusCreateTransChargeBack(oldTrans.getId(), "1");
                        } else {
                            cachedSmeTrans.setStatus(Constants.TransStatus.MAKER_SUCCESS);
                            cachedSmeTrans.setTranxNote("Lập lệnh thành công");
                            smeTransRepository.save(cachedSmeTrans);
                            type = "1";
                            //lưu trạng thái để khóa giao dịch đã tra soát
                            updateStatusCreateTransChargeBack(oldTrans.getId(), "0");
                        }

                        transactionLimitService.saveCheckTransLimit(
                                user,
                                req,
                                cachedSmeTrans.getTranxType(),
                                metadata.getCheckLimitTrans().getAmount(),
                                metadata.getCheckLimitTrans().getCcy(),
                                req.getAuthenType(),
                                type);

                        // end
                        redisCacheService.pushTxn(req, req.getTranToken(), cachedSmeTrans);
                        smeTransRepository.save(cachedSmeTrans);
                        String metaStr = cachedSmeTrans.getMetadata();
                        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

                        // Lưu chức năng gần đây
                        commonService.saveFuncRecent(cachedSmeTrans.getTranxType(), user.getUsername(), user.getRoleType(), user.getConfirmType(), req.getSource());

                        // Trả thông tin cho client
                        dataRp.setContact(
                                commonService.isSavedBene(
                                        cachedSmeTrans.getTranxType(),
                                        cachedSmeTrans.getBeneBankCode(),
                                        user.getUsername(),
                                        null,
                                        cachedSmeTrans.getToAcc(),
                                        null,
                                        null));
                        dataRp.setTranxId(StringUtils.isBlank(metaData.getTsoLRef()) ? String.valueOf(cachedSmeTrans.getId()) : metaData.getTsoLRef());
                        dataRp.setTranDate(CommonUtils.formatDate(new Date()));
                        dataRp.setFee(metadata.getFee().getOriginAmount());
                        dataRp.setExchangeFee(new BigDecimal(metadata.getFee().getAmount()));
                        dataRp.setVat(metadata.getFee().getOriginVatAmount());
                        dataRp.setExchangeVat(new BigDecimal(metadata.getFee().getVatAmount()));
                        dataRp.setTotalFee(
                                metadata.getFee().getOriginAmount() + metadata.getFee().getOriginVatAmount());
                        dataRp.setExchangeTotalFee(
                                new BigDecimal(metadata.getFee().getAmount() + metadata.getFee().getVatAmount()));
//                        dataRp.setTotalAmount(metadata.getDebitAccount().getOriginAmount());
//                        dataRp.setExchangeTotalAmount(
//                                new BigDecimal(metadata.getDebitAccount().getAmountVND()).longValue());
                        dataRp.setAmount(cachedSmeTrans.getAmount());
                        dataRp.setExchangeAmount(
                                new BigDecimal(cachedSmeTrans.getTotalAmount()).longValue());
                        dataRp.setClientRqCcy(metadata.getClientRqCcy());
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
            log.info("Error: ", e);
        } finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse getDetailChargeback(GetDetailTransChargebackRequest rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                // Kiểm tra trạng thái của user
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        //get trans response to client
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String[] tellerSeqArr = rq.getReference().split("-");
                        String[] hostdateArr = rq.getTransactionDate().split("T");
//                        LocalDateTime fromDate = LocalDateTime.parse(hostdateArr[0].concat(" 00:00:00"), format).plusDays(-8);
//                        LocalDateTime toDate = LocalDateTime.parse(hostdateArr[0].concat(" 00:00:00"), format).plusDays(2);
                        LocalDateTime fromDate = LocalDateTime.now().plusDays(-8);
                        LocalDateTime toDate = LocalDateTime.now().plusDays(1);
                        List<SmeTrans> listTransOn = smeTransRepository.findByTranxTimeBetweenAndCifInt(fromDate, toDate, Integer.parseInt(user.getCif()));
                        SmeTrans trans = null;
                        if (listTransOn.size() > 0) {
                            log.info("list size on {}", listTransOn.size());
                            //lọc theo đk và trả về kết quả
                            trans = getSmeTransFromDb(listTransOn, tellerSeqArr[0].trim(), hostdateArr[0], tellerSeqArr[1].trim(), rq.getPcTime());
                        }
                        if (trans == null) {
                            log.info("data in db on not found, find in db off");
                            List<SmeTransOffline> listTransOff = smeTransRepositoryOffline.findByTranxTimeBetweenAndCifInt(fromDate, toDate, Integer.parseInt(user.getCif()));
                            if (listTransOff.size() > 0) {
                                log.info("list size off {}", listTransOff.size());
                                List<SmeTrans> smeTransListTwo = modelMapper.map(listTransOff, new TypeToken<List<SmeTrans>>() {
                                }.getType());
                                //lọc theo đk và trả về kết quả
                                trans = getSmeTransFromDb(smeTransListTwo, tellerSeqArr[0].trim(), hostdateArr[0], tellerSeqArr[1].trim(), rq.getPcTime());
                            }
                        }
                        if (trans == null) {
                            log.info("data not found");
                            resp.setCode(Constants.ResCode.INFO_82);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_82, "VI"));
                            return resp;
                        }

                        //push case trans
                        String keyTrans = "detail_trans_chargeback" + rq.getUser() + rq.getSessionId();
                        redisCacheService.set(keyTrans, gson.toJson(trans), 30, TimeUnit.MINUTES);

                        TransactionMetaDataDTO metaData = gson.fromJson(trans.getMetadata(), TransactionMetaDataDTO.class);
                        //kiểm tra xem giao dịch đã lập lệnh tra soát chưa
                        if ("0".equals(metaData.getStatusCreateChargeback())) {
                            log.info("giao dịch đã được khởi tạo tra soát");
                            resp.setCode(Constants.ResCode.INFO_78);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_78, rq.getLang()));
                            return resp;
                        }

                        // Kiếm tra role type được quyền thực hiện chức năng hay không
                        String service2CheckRole = StringUtils.isNotBlank(metaData.getService2CheckRole()) ? metaData.getService2CheckRole() : trans.getTranxType();
                        resp =
                                transactionService.validateUserAndServiceCode(
                                        resp, user, service2CheckRole, "1", rq.getLang(), null);
                        if (!resp.getCode().equals(Constants.ResCode.INFO_00)) {
                            resp.setCode(Constants.ResCode.INFO_81);
                            resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_81, rq.getLang()));
                            return resp;
                        }

                        //get detail from bank
                        GetTransChargebackInfoBankRequest getTransChargebackInfoReq = GetTransChargebackInfoBankRequest.builder()
                                .teller(trans.getTeller())
                                .sequence(metaData.getSequence())
                                .cif(trans.getCifInt())
                                .hostDate(metaData.getHostDate())
                                .remark(trans.getTranxRemark())
                                .amount(trans.getAmount())
                                .bankAccount(trans.getFromAcc())
                                .pcTime(metaData.getPcTime())
                                .type_request(0)
                                .build();

                        GetTransChargebackInfoBankResponse getTransChargebackInfoResp = serviceGWClient.getDetailTransChargeback(getTransChargebackInfoReq);
                        if (!getTransChargebackInfoResp.getResponseStatus().getIsSuccess()) {
                            if ("15".equals(getTransChargebackInfoResp.getResponseStatus().getResCode())) {
                                resp.setCode(Constants.ResCode.TSOL_15);
                                resp.setMessage(commonService.getMessage(Constants.MessageCode.TSOL_15, rq.getLang()));
                                return resp;
                            } else if ("2".equals(getTransChargebackInfoResp.getResponseStatus().getResCode())) {
                                resp.setCode(Constants.ResCode.TSOL_2);
                                resp.setMessage(commonService.getMessage(Constants.MessageCode.TSOL_2, rq.getLang()));
                                return resp;
                            }
                            resp.setCode(getTransChargebackInfoResp.getResponseStatus().getResCode());
                            resp.setMessage(getTransChargebackInfoResp.getResponseStatus().getResMessage());
                            return resp;
                        }
                        //get status trans from bank
                        GetIBPSTransStatusBankRequest getIBPSTransStatusBankRequest = GetIBPSTransStatusBankRequest.builder()
                                .hostDate(metaData.getHostDate())
                                .teller(trans.getTeller())
                                .sequence(metaData.getSequence())
                                .pctime(metaData.getPcTime())
                                .branch(getTransChargebackInfoResp.getTransDetail().getDebitbranch() + "")
                                .build();

                        String statusTransResp = null;
                        String serialNo = null;
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(trans.getTranxType())
                                || Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(trans.getTranxType())
                                || Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED.equals(trans.getTranxType())) {
                            GetIBPSTransStatusBankResponse getIBPSTransStatusBankResponse = serviceGWClient.getIBPSTransStatus(getIBPSTransStatusBankRequest);
                            if (!getIBPSTransStatusBankResponse.getResponseStatus().getIsSuccess()) {
                                resp.setCode(getIBPSTransStatusBankResponse.getResponseStatus().getResCode());
                                resp.setMessage(getIBPSTransStatusBankResponse.getResponseStatus().getResMessage());
                                return resp;
                            }
                            statusTransResp = String.valueOf(getIBPSTransStatusBankResponse.getStatus());
                            serialNo = getIBPSTransStatusBankResponse.getSerialNo();
                        }

                        //push response bank to cache
                        String key = "response_detail_trans_chargeback_" + rq.getUser() + rq.getSessionId();
                        redisCacheService.set(key, gson.toJson(getTransChargebackInfoResp), 30, TimeUnit.MINUTES);
                        TransDetailChargebackDTO transDetail = getTransChargebackInfoResp.getTransDetail();
                        List<RequestTSObjectDTO> listRequestTSObject = getTransChargebackInfoResp.getRelationshipObj().getListRequestTSObject();
                        //convert phí trả client
                        String vatExamptFlag = commonService.getVatExemptFlag(rq);
                        boolean isFreeVat = "Y".equalsIgnoreCase(vatExamptFlag);
                        for (RequestTSObjectDTO requestTSObjectDTO : listRequestTSObject) {
                            List<ReasonObjectDTO> listReasonObject = requestTSObjectDTO.getListReasonObject();
                            for (ReasonObjectDTO reasonObjectDTO : listReasonObject) {
                                if (isFreeVat) {
                                    String tsolFeeDiscountStr = commonService.getConfig("TSOL_FEE_DISCOUNT", "0");
                                    double tsolFeeDiscountDouble = Double.parseDouble(tsolFeeDiscountStr) / 100;
                                    BigDecimal feeTSOL = new BigDecimal(reasonObjectDTO.getFeeTSOL());
                                    BigDecimal totalFeeDiscount = new BigDecimal(1 + tsolFeeDiscountDouble);
                                    double feeamT_FLAT_VND = feeTSOL.divide(totalFeeDiscount, 0, RoundingMode.HALF_UP).doubleValue(); //vnd
                                    reasonObjectDTO.setFeeTSOL(feeamT_FLAT_VND);
                                }
                            }
                        }

                        //build data response to client
                        GetDetailTransChargebackResponse dataResp = GetDetailTransChargebackResponse.builder()
                                .serviceCode(trans.getTranxType())
                                .serviceName(getTransChargebackInfoResp.getServiceName())
                                .serviceNameEn(getTransChargebackInfoResp.getServiceName_EN())
                                .channel("VCB DigiBiz")
                                .createdDate(transDetail.getDatE_TIME())
                                .amount(transDetail.getAmount())
                                .creditAcc(transDetail.getCreditaccount())
                                .creditName(transDetail.getCreditname())
                                .bankCode(transDetail.getBeN_BANK_CODE())
                                .bankName(transDetail.getBeN_BANK_NAME())
                                .content(transDetail.getRemark())
                                .idNo(transDetail.getIdno())
                                .issuePlace(transDetail.getIssuE_PLACE())
                                .issueDate(transDetail.getIssuE_DATE())
                                .status(statusTransResp)
                                .serialNo(serialNo)
                                .currency(getTransChargebackInfoResp.getTransDetail().getCurrency())
                                .teller(trans.getTeller())
                                .sequence(metaData.getSequence())
                                .hostdate(hostdateArr[0])
                                .pcTime(metaData.getPcTime())
                                .listRequestTSObject(listRequestTSObject)
                                .build();
                        if (Constants.ServiceCode.CASH_TRANS.equals(trans.getTranxType()))
                            dataResp.setCreditAcc(null);
                        else
                            dataResp.setIssueDate(null);
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(trans.getTranxType()) && StringUtils.isNotBlank(metaData.getCardMaskingNumber()))
                            dataResp.setCreditAcc(metaData.getCardMaskingNumber());
                        resp.setData(dataResp);
                        break;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public BaseClientResponse getListTransChargeback(GetListTransChargebackReq rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                // Kiểm tra trạng thái của user
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        //get list from bank
                        GetListTransChargebackBankRequest getListTransChargebackBankRequest = GetListTransChargebackBankRequest.builder()
                                .froM_DATE(rq.getFromDate())
                                .tO_DATE(rq.getToDate())
                                .cuS_CIF(user.getCifInt())
                                .build();
                        GetListTransChargebackBankResponse getListTransChargebackBankResponse = serviceGWClient.getListTransChargeback(getListTransChargebackBankRequest);
                        if (!getListTransChargebackBankResponse.getResponseStatus().getIsSuccess()) {
                            resp.setCode(getListTransChargebackBankResponse.getResponseStatus().getResCode());
                            resp.setMessage(getListTransChargebackBankResponse.getResponseStatus().getResMessage());
                            return resp;
                        }
                        List<GetListTransChargebackDTO> listTSOL_TRANSACTION =
                                getListTransChargebackBankResponse.getListTSOL_TRANSACTION().stream()
                                        .filter(p -> {
                                                    if (org.apache.commons.lang3.StringUtils.isNotBlank(rq.getTsolRef())) {
                                                        return rq.getTsolRef().equals(p.getTsoL_REF());
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                        ).filter(p -> {
                                                    if (org.apache.commons.lang3.StringUtils.isNotBlank(rq.getMaker())) {
                                                        String[] userArr = p.getCuS_CODE().split("\\|");
                                                        return rq.getMaker().equals(userArr[0]);
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                        ).filter(p -> {
                                                    if (org.apache.commons.lang3.StringUtils.isNotBlank(rq.getChecker())) {
                                                        String[] userArr = p.getCuS_CODE().split("\\|");
                                                        if (userArr.length > 1) {
                                                            return rq.getChecker().equals(userArr[1]);
                                                        } else {
                                                            return false;
                                                        }
                                                    } else {
                                                        return true;
                                                    }
                                                }
                                        ).collect(Collectors.toList());
                        //build response to client
                        List<ListTransChargebackRespClientDTO> listTransChargebackRespClientDTOList = new ArrayList<>();
                        List<TransactionDTO> recordsCache = new ArrayList<>();
                        if (listTSOL_TRANSACTION != null && listTSOL_TRANSACTION.size() > 0) {
                            for (GetListTransChargebackDTO detail : listTSOL_TRANSACTION) {
                                //build response toclient
                                String date = detail.getCreatE_DATE();
                                String[] dateArr = date.split("T");
                                String dateResp = CommonUtils.TimeUtils.format("yyyy-MM-dd", "dd/MM/yyyy", dateArr[0]);
                                ListTransChargebackRespClientDTO dataDto = ListTransChargebackRespClientDTO.builder()
                                        .createdDate(dateResp)
                                        .tsolRef(detail.getTsoL_REF())
                                        .amount(detail.getTraN_AMOUNT())
                                        .ccy(detail.getTraN_CURRENCY())
                                        .status(detail.getTsoL_STATUS())
                                        .requestTSName(detail.getRequesT_NAME())
                                        .requestTSName_EN(detail.getRequesT_NAME_EN())
                                        .build();
                                listTransChargebackRespClientDTOList.add(dataDto);
                                //add data đẩy lên cache
                                TransactionDTO transactionDTO = new TransactionDTO();
                                transactionDTO.setTsoLRef(detail.getTsoL_REF());
                                transactionDTO.setAmountTrans(detail.getTraN_AMOUNT());
                                transactionDTO.setCcyTrans(detail.getTraN_CURRENCY());
                                transactionDTO.setRequestTSID(String.valueOf(detail.getRequesT_CODE()));
                                transactionDTO.setRequestTSName(detail.getRequesT_NAME());
                                transactionDTO.setRequestTSNameEN(detail.getRequesT_NAME_EN());
                                transactionDTO.setStatusChargeback(detail.getTsoL_STATUS());
                                transactionDTO.setTranxTime(dateResp);
                                recordsCache.add(transactionDTO);
                            }
                        }
                        //build push cache
                        CacheTransCharegebackDTO cacheTransCharegebackDTO = CacheTransCharegebackDTO.builder()
                                .records(recordsCache)
                                .totalRecords(recordsCache.size())
                                .build();
                        redisCacheService.set("LIST_RECORDS_" + user.getCusUserId() + "_" + Constants.PagePrintType.LIST_CHARGEBACK,
                                gson.toJson(cacheTransCharegebackDTO),
                                15,
                                TimeUnit.MINUTES);
                        GetListTransChargebackResponse dataResp = GetListTransChargebackResponse.builder()
                                .listTransChargeback(listTransChargebackRespClientDTOList)
                                .build();
                        resp.setData(dataResp);
                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public BaseClientResponse getDetailCreateTransCharegeback(GetDetailCreateTransChargebackReq rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            if (user != null) {
                // Kiểm tra trạng thái của user
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        //get detail from bank
                        GetDetailCreateTransChargebackBankReq reqBank = GetDetailCreateTransChargebackBankReq.builder()
                                .tsoL_REF(rq.getTsolRef())
                                .build();
                        GetDetailCreateTransChargebackBankResponse respBank = serviceGWClient.getCreateTransChargeback(reqBank);
                        if (!respBank.getResponseStatus().getIsSuccess()) {
                            resp.setCode(respBank.getResponseStatus().getResCode());
                            resp.setMessage(respBank.getResponseStatus().getResMessage());
                            return resp;
                        }
                        //build response to client
                        TransDetailCharebackDTO transSme = TransDetailCharebackDTO.builder()
                                .serviceCode(respBank.getOriG_TRAN_DETAIL_OBJ().getTrantype())
                                .chanel("VCB DigiBiz")
                                .amount(respBank.getOriG_TRAN_DETAIL_OBJ().getAmount())
                                .ccy(respBank.getOriG_TRAN_DETAIL_OBJ().getCurrency())
                                .createdDate(respBank.getOriG_TRAN_DETAIL_OBJ().getDatE_TIME())
                                .creditName(respBank.getOriG_TRAN_DETAIL_OBJ().getCreditname())
                                .beneBankCode(respBank.getOriG_TRAN_DETAIL_OBJ().getBeN_BANK_CODE())
                                .beneBankName(respBank.getOriG_TRAN_DETAIL_OBJ().getBeN_BANK_NAME())
                                .remark(respBank.getOriG_TRAN_DETAIL_OBJ().getRemark())
                                .idNo(respBank.getOriG_TRAN_DETAIL_OBJ().getIdno())
                                .issuePlace(respBank.getOriG_TRAN_DETAIL_OBJ().getIssuE_PLACE())
                                .creditAcc(respBank.getOriG_TRAN_DETAIL_OBJ().getCreditaccount())
                                .build();
                        if (StringUtils.isNotBlank(respBank.getOriG_TRAN_DETAIL_OBJ().getIdno()))
                            transSme.setIssueDate(respBank.getOriG_TRAN_DETAIL_OBJ().getIssuE_DATE().split("T")[0]);
                        if (Constants.ServiceCode.CASH_TRANS.equals(respBank.getOriG_TRAN_DETAIL_OBJ().getTrantype()))
                            transSme.setCreditAcc(null);
                        Optional<MbService> mbService = mbServiceRepository.findByServiceCode(respBank.getOriG_TRAN_DETAIL_OBJ().getTrantype());
                        if (mbService.isPresent()) {
                            transSme.setServiceName(mbService.get().getServiceName());
                        }

                        FeeTransDetailChargebackRespDTO feeTsolDetail = gson.fromJson(respBank.getTsoL_Transaction().getFeE_TRAN_DETAIL(), FeeTransDetailChargebackRespDTO.class);
                        DataChangeTransChargebackRespDTO dataChange = gson.fromJson(respBank.getTsoL_Transaction().getTsoL_TRAN_DETAIL(), DataChangeTransChargebackRespDTO.class);

                        TransDetailCreateCharebackDTO smeChargeback = TransDetailCreateCharebackDTO.builder()
                                .tsolRef(respBank.getTsoL_Transaction().getTsoL_REF())
                                .requestTSID(respBank.getTsoL_Transaction().getRequesT_CODE())
                                .requestTSName(respBank.getTsoL_Transaction().getRequesT_NAME())
                                .reasonID(respBank.getTsoL_Transaction().getReasoN_CODE())
                                .reasonName(respBank.getTsoL_Transaction().getReasoN_NAME())
                                .debitAcc(feeTsolDetail.getAccNo())
                                .feeFLAT(feeTsolDetail.getFeeAmtFlatVND())
                                .feeVAT(feeTsolDetail.getFeeAmtVatVND())
                                .ccy("VND")
                                .tsolComment(respBank.getTsoL_Transaction().getTsoL_COMMENT())
                                .tsolDetail(dataChange)
                                .build();

                        String userArr[] = respBank.getTsoL_Transaction().getCuS_CODE().split("\\|");
                        smeChargeback.setCreatedUser(userArr[0]);
                        if (userArr.length > 1) {
                            smeChargeback.setApprovedUser(userArr[1]);
                        }

                        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                        LocalDateTime approvedDate = LocalDateTime.parse(respBank.getTsoL_Transaction().getCreatE_DATE().split("T")[0] + " " + respBank.getTsoL_Transaction().getCreatE_DATE().split("T")[1], formatDateTime);

                        String[] hostDateArr = respBank.getTsoL_Transaction().getCreatE_DATE().split("T");
                        LocalDateTime hostDate = LocalDateTime.parse(hostDateArr[0] + " " + "00:00:00", formatDateTime);
                        LocalDateTime fromDate = hostDate.plusDays(-8);
                        LocalDateTime toDate = hostDate.plusDays(2);
                        SmeTrans smeTrans;
                        //pilot va live
//                        Optional<SmeTrans> smeTransOnOpt = smeTransRepository.findByTranxTimeBetweenAndTsolRef(fromDate, toDate, respBank.getTsoL_Transaction().getTsoL_REF());
                        //uat va sit
                        Optional<SmeTrans> smeTransOnOpt = smeTransRepository.findByTsolRef(respBank.getTsoL_Transaction().getTsoL_REF());
                        if (!smeTransOnOpt.isPresent()) {
                            log.info("TSOL_REF not found in db on, find in db off");
                            //pilot va live
                            Optional<SmeTransOffline> smeTransOffOpt = smeTransRepositoryOffline.findByTranxTimeBetweenAndTsolRef(fromDate, toDate, respBank.getTsoL_Transaction().getTsoL_REF());
                            //uat va sit
//                            Optional<SmeTransOffline> smeTransOffOpt = smeTransRepositoryOffline.findByTsolRef(respBank.getTsoL_Transaction().getTsoL_REF());
                            if (!smeTransOffOpt.isPresent()) {
                                log.info("TSOL_REF not found in db off");
                                resp.setCode(Constants.ResCode.INFO_82);
                                resp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_82, rq.getLang()));
                                return resp;
                            }
                            SmeTransOffline smeTransOff = smeTransOffOpt.get();
                            smeTrans = modelMapper.map(smeTransOff, SmeTrans.class);
                        } else {
                            smeTrans = smeTransOnOpt.get();
                        }
                        DateTimeFormatter dateFormatToClient = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        if (smeTrans.getApprovedDate() != null) {
                            smeChargeback.setApprovedDate(smeTrans.getApprovedDate().format(dateFormatToClient));
                        }
                        smeChargeback.setCreatedDate(smeTrans.getTranxTime().format(dateFormatToClient));
                        TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(smeTrans.getTranxType()) && StringUtils.isNotBlank(metaDataDTO.getCardMaskingNumber()))
                            transSme.setCreditAcc(metaDataDTO.getCardMaskingNumber());

                        GetDetailCreateTransChargebackResp respData = GetDetailCreateTransChargebackResp.builder()
                                .transSme(transSme)
                                .transChargeback(smeChargeback)
                                .status(respBank.getTsoL_Transaction().getTsoL_STATUS())
                                .build();
                        resp.setData(respData);
                        return resp;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public BaseClientResponse checkerInitTransChargeback(BaseCheckerInitRequest rq) {
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
                        if (smeTrans == null
                                || (!"5".equals(smeTrans.getStatus()) && !"10".equals(smeTrans.getStatus()))) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_0204,
                                    commonService.getMessage(Constants.MessageCode.CONFIRM_TRANS_FAIL, rq.getLang()));
                        }
                        if (!smeTrans.getCifNo().equals(user.getCif())) {
                            return new BaseClientResponse(
                                    Constants.ResCode.ERROR_96,
                                    commonService.getMessage(Constants.MessageCode.INVALID_DATA, rq.getLang()));
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

                        // Kiểm tra quyền giao dịch tài khoản nguồn
                        rp =
                                transactionService.validateTransAuthorityForAccount(
                                        rp, user, finalSmeTrans.getFromAcc(), smeTrans.getTranxType(), rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }

                        // Lấy thông tin chi tiết tài khoản debit
                        log.info("Get Debit account details for {}", smeTrans.getFromAcc());
                        AccountDetailBankRequest accDetailsReq = new AccountDetailBankRequest();
                        accDetailsReq.setAccountNo(smeTrans.getFromAcc());
                        accDetailsReq.setAccountType(acc.getAccountType());
                        accDetailsReq.setAlias(smeTrans.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()));
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
                                        smeTrans.getFromAcc(),
                                        acc.getAccountType(),
                                        smeTrans.getFromAcc().equalsIgnoreCase(acc.getAccountAlias()),
                                        true,
                                        rq.getLang());
                        if (!rp.getCode().equals(Constants.ResCode.INFO_00)) {
                            return rp;
                        }
                        // Kiểm tra số dư tài khoản debit
                        String minBal =
                                "VND".equals(debitBankResp.getCurCode())
                                        ? commonService.getConfig("MIN_BALANCE", "50000")
                                        : commonService.getConfig("MIN_BALANCE_FOR", "10");
                        log.info("minBal: {}", minBal);
                        if ((new BigDecimal(debitBankResp.getAvaiableAmount()))
                                .subtract(new BigDecimal(minBal))
                                .doubleValue()
                                < smeTrans.getAmount()) {
                            rp.setCode(Constants.ResCode.ERROR_112);
                            rp.setMessage(
                                    commonService.getMessage(Constants.MessageCode.ERROR_112, rq.getLang()));
                            return rp;
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
                        // Khởi tạo ptxt
                        String tranToken = transactionService.genTranToken();
                        InitAuthenResponse intAuthen =
                                transactionService.intAuthen(
                                        user,
                                        rq,
                                        checkAuthen.getAuthenMethod(),
                                        smeTrans.getFromAcc(),
                                        smeTrans.getFromAcc(),
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
                        dataRp.setToAccount(smeTrans.getToAcc());
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

    public BaseClientResponse checkerConfirmTransChargeback(BaseConfirmRq req) {
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
                                        resp, user, cachedSmeTrans.getTranxType(), "2", req.getLang(), cachedSmeTrans.getCreatedUser());
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
                        resp = transactionService.execTransChargeback(req, resp, cachedSmeTrans);
                        //            if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                        //              return resp;
                        //            }
                        log.info(gson.toJson(resp));
                        TransactionMetaDataDTO transactionMetaDataDTO =
                                gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                        // log.info(gson.toJson(transactionMetaDataDTO));
                        TransConfirmRes transConfirmRes =
                                TransConfirmRes.builder()
                                        .tranxId(StringUtils.isBlank(transactionMetaDataDTO.getTsoLRef()) ? String.valueOf(cachedSmeTrans.getId()) : transactionMetaDataDTO.getTsoLRef())
                                        .tranxDate(CommonUtils.getDate("HH:mm dd/MM/yyyy"))
                                        .build();
                        resp.setData(transConfirmRes);

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
                        //update trạng thái giao dịch cũ thành đã duyệt tra soát
                        updateStatusCreateTransChargeBack(metadata.getIdOld(), "1");

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
            log.info("Error: ", e);
        } finally {
            if (isDeleteCache)
                redisCacheService.delete(req.getTranxId());
        }
        return resp;
    }

    public BaseClientResponse getListServiceCodeChargeback(BaseClientRequest rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            List<String> resultServiceCode = new ArrayList<>();
            //danh sách dịch vụ đc phép tra cứu tra soát
            List<String> listServiceCodeChargeback = Arrays.asList(
                    Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SAME_CIF,
                    Constants.ServiceCode.TRANS_IN_VIA_ACCNO_DIFF_CIF,
                    Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE,
                    Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED,
                    Constants.ServiceCode.TRANS_OUT_VIA_ACCNO,
                    Constants.ServiceCode.FAST_TRANS_VIA_ACCNO,
//                    Constants.ServiceCode.FAST_TRANS_VIA_CARDNO,
                    Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE,
                    Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED,
                    Constants.ServiceCode.TRANSFER_WALLET,
                    Constants.ServiceCode.CASH_TRANS,
                    Constants.ServiceCode.BILL_PAYMENT
            );
            if (user != null) {
                // Kiểm tra trạng thái của user
                switch (user.getCusUserStatus()) {
                    case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                        //admin tra ve full dich vu
                        if (user.getRoleType().equals(Constants.UserRole.ADMIN)) {
                            List<MbService> listResult = mbServiceRepository.findByServiceCodeIn(listServiceCodeChargeback);
                            GetListServiceCodeChargebackResp dataRp = GetListServiceCodeChargebackResp.builder()
                                    .serviceCodes(listResult)
                                    .build();
                            resp.setData(dataRp);
                            return resp;
                        }

                        //lấy danh sách dịch vụ với maker
                        if (user.getRoleType().equals(Constants.UserRole.MAKER) || user.getRoleType().equals(Constants.UserRole.ALL)) {
                            List<SmeMakerServiceRole> listMkSerRole =
                                    smeMakerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                            user.getUsername());
                            if (listMkSerRole.isEmpty()) {//tra full dich vu
                                List<MbService> listResult = mbServiceRepository.findByServiceCodeIn(listServiceCodeChargeback);
                                GetListServiceCodeChargebackResp dataRp = GetListServiceCodeChargebackResp.builder()
                                        .serviceCodes(listResult)
                                        .build();
                                resp.setData(dataRp);
                                return resp;
                            } else {
                                for (String serviceCode : listServiceCodeChargeback) {
                                    Optional<SmeMakerServiceRole> mkSerRoleOpt =
                                            listMkSerRole.stream()
                                                    .filter(
                                                            e ->
                                                                    e.getCusUserId() == user.getCusUserId()
                                                                            && serviceCode.equals(e.getServiceCode())
                                                                            && "1".equals(e.getIsTrans())
                                                                            && "1".equals(e.getStatus()))
                                                    .findFirst();
                                    if (mkSerRoleOpt.isPresent()) {
                                        resultServiceCode.add(mkSerRoleOpt.get().getServiceCode());
                                    }
                                }
                                List<MbService> listResult = mbServiceRepository.findByServiceCodeIn(resultServiceCode);
                                GetListServiceCodeChargebackResp dataRp = GetListServiceCodeChargebackResp.builder()
                                        .serviceCodes(listResult)
                                        .build();
                                resp.setData(dataRp);
                            }
                        }

                        //lấy danh sách dịch vụ với checker
                        if (user.getRoleType().equals(Constants.UserRole.CHECKER)) {
                            List<SmeCheckerServiceRole> listCkSerRole =
                                    smeCheckerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                            user.getUsername());
                            if (listCkSerRole.isEmpty()) {//tra full dich vu
                                List<MbService> listResult = mbServiceRepository.findByServiceCodeIn(listServiceCodeChargeback);
                                GetListServiceCodeChargebackResp dataRp = GetListServiceCodeChargebackResp.builder()
                                        .serviceCodes(listResult)
                                        .build();
                                resp.setData(dataRp);
                                return resp;
                            } else {
                                for (String serviceCode : listServiceCodeChargeback) {
                                    Optional<SmeCheckerServiceRole> ckSerRoleOpt =
                                            listCkSerRole.stream()
                                                    .filter(
                                                            e ->
                                                                    e.getCusUserId() == user.getCusUserId()
                                                                            && serviceCode.equals(e.getServiceCode())
                                                                            && "1".equals(e.getIsTrans())
                                                                            && "1".equals(e.getStatus()))
                                                    .findFirst();
                                    if (ckSerRoleOpt.isPresent()) {
                                        resultServiceCode.add(ckSerRoleOpt.get().getServiceCode());
                                    }
                                }
                                List<MbService> listResult = mbServiceRepository.findByServiceCodeIn(resultServiceCode);
                                GetListServiceCodeChargebackResp dataRp = GetListServiceCodeChargebackResp.builder()
                                        .serviceCodes(listResult)
                                        .build();
                                resp.setData(dataRp);
                            }
                        }
                        break;
                    default:
                        log.info("Invalid user status");
                        resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                        resp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                        break;
                }
            } else {
                log.info("User not found");
                resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
                resp.setMessage(
                        commonService.getMessage(
                                vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
            }
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public BaseClientResponse getListMakerChecker(BaseClientRequest rq) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, rq.getLang()));
        try {
            SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
            List<SmeMakerServiceRoleDTO> listMakerResult = new ArrayList<>();
            List<SmeCheckerServiceRoleDTO> listCheckerResult = new ArrayList<>();
            // Kiểm tra trạng thái của user
            switch (user.getCusUserStatus()) {
                case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                    if (user.getRoleType().equals(Constants.UserRole.MAKER)) {
                        //add list maker
                        SmeMakerServiceRoleDTO makerServiceRoleDTO = SmeMakerServiceRoleDTO.builder()
                                .cusUsername(user.getUsername())
                                .build();
                        listMakerResult.add(makerServiceRoleDTO);
                        //add list checker
                        List<SmeCustomerUser> listUserChecker = smeCustomerUserRepository.findByCifAndRoleType(user.getCif(), Constants.UserRole.CHECKER);
                        for (SmeCustomerUser userChecker : listUserChecker) {
                            List<SmeCheckerServiceRole> listCkSerRole =
                                    smeCheckerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                            userChecker.getUsername());
                            if (listCkSerRole.isEmpty()) {
                                log.info("List maker service role is empty ----> Allowed trans execution by default");
                                SmeCheckerServiceRoleDTO checkerServiceRoleDTO = SmeCheckerServiceRoleDTO.builder()
                                        .cusUsername(userChecker.getUsername())
                                        .build();
                                listCheckerResult.add(checkerServiceRoleDTO);
                            } else {
                                Optional<SmeCheckerServiceRole> ckSerRoleOpt =
                                        listCkSerRole.stream()
                                                .filter(
                                                        e ->
                                                                e.getCusUserId() == userChecker.getCusUserId()
                                                                        && Constants.ServiceCode.TRANS_CHARGEBACK.equals(e.getServiceCode())
                                                                        && e.getMakerUser().equals(user.getUsername())
                                                                        && "1".equals(e.getIsTrans())
                                                                        && "1".equals(e.getStatus()))
                                                .findFirst();
                                if (ckSerRoleOpt.isPresent()) {
                                    SmeCheckerServiceRoleDTO checkerServiceRoleDTO = SmeCheckerServiceRoleDTO.builder()
                                            .cusUsername(userChecker.getUsername())
                                            .build();
                                    listCheckerResult.add(checkerServiceRoleDTO);
                                }
                            }
                        }
                    } else if (user.getRoleType().equals(Constants.UserRole.CHECKER)) {
                        //add list checker
                        SmeCheckerServiceRoleDTO checkerServiceRoleDTO = SmeCheckerServiceRoleDTO.builder()
                                .cusUsername(user.getUsername())
                                .build();
                        listCheckerResult.add(checkerServiceRoleDTO);
                        //add list maker
                        List<SmeCustomerUser> listUserMaker = smeCustomerUserRepository.findByCifAndRoleType(user.getCif(), Constants.UserRole.MAKER);
                        List<SmeCheckerServiceRole> listCkSerRole =
                                smeCheckerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                        user.getUsername());
                        if (listCkSerRole.isEmpty()) {
                            //add all maker
                            listUserMaker.forEach(p -> {
                                SmeMakerServiceRoleDTO makerServiceRoleDTO = SmeMakerServiceRoleDTO.builder()
                                        .cusUsername(p.getUsername())
                                        .build();
                                listMakerResult.add(makerServiceRoleDTO);
                            });
                        } else {
                            for (SmeCustomerUser userMaker : listUserMaker) {
                                Optional<SmeCheckerServiceRole> ckSerRoleOpt =
                                        listCkSerRole.stream()
                                                .filter(
                                                        e ->
                                                                e.getCusUserId() == user.getCusUserId()
                                                                        && Constants.ServiceCode.TRANS_CHARGEBACK.equals(e.getServiceCode())
                                                                        && e.getMakerUser().equals(userMaker.getUsername())
                                                                        && "1".equals(e.getIsTrans())
                                                                        && "1".equals(e.getStatus()))
                                                .findFirst();
                                if (ckSerRoleOpt.isPresent()) {
                                    SmeMakerServiceRoleDTO makerServiceRoleDTO = SmeMakerServiceRoleDTO.builder()
                                            .cusUsername(userMaker.getUsername())
                                            .build();
                                    listMakerResult.add(makerServiceRoleDTO);
                                }
                            }
                        }
                    } else if (user.getRoleType().equals(Constants.UserRole.ALL)) {
                        SmeMakerServiceRoleDTO makerServiceRoleDTO = SmeMakerServiceRoleDTO.builder()
                                .cusUsername(user.getUsername())
                                .build();
                        listMakerResult.add(makerServiceRoleDTO);
                    } else {
                        //show all maker, checker
                        List<SmeCustomerUser> listUserMaker = smeCustomerUserRepository.findByCifAndRoleType(user.getCif(), Constants.UserRole.MAKER);
                        List<SmeCustomerUser> listUserChecker = smeCustomerUserRepository.findByCifAndRoleType(user.getCif(), Constants.UserRole.CHECKER);
                        listUserMaker.forEach(p -> {
                            SmeMakerServiceRoleDTO makerServiceRoleDTO = SmeMakerServiceRoleDTO.builder()
                                    .cusUsername(p.getUsername())
                                    .build();
                            listMakerResult.add(makerServiceRoleDTO);
                        });
                        listUserChecker.forEach(p -> {
                            SmeCheckerServiceRoleDTO checkerServiceRoleDTO = SmeCheckerServiceRoleDTO.builder()
                                    .cusUsername(p.getUsername())
                                    .build();
                            listCheckerResult.add(checkerServiceRoleDTO);
                        });
                    }
                    GetListMakerCheckerChargebackResp dataResp = GetListMakerCheckerChargebackResp.builder()
                            .listMaker(listMakerResult)
                            .listChecker(listCheckerResult)
                            .build();
                    resp.setData(dataResp);
                    return resp;
                default:
                    log.info("Invalid user status");
                    resp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                    resp.setMessage(
                            commonService.getMessage(
                                    vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                    break;
            }

        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
            log.info("Error: ", e);
        }
        return resp;
    }

    public AccountHistoryResponse queryAccountHistory(
            BaseClientResponse baseResp, AccountHistoryRequest rq) {
        SmeCustomerUser user = redisCacheService.getCustomerUser(rq);
        if (user != null) {
            // 2. Kiểm tra trạng thái của user
            switch (user.getCusUserStatus()) {
                case vn.vnpay.commoninterface.common.Constants.UserStatus.ACTIVE:
                    // Check khóa quyền truy vấn
                    log.info("Get view authorities from DB for user {}", user.getUsername());
                    List<SmeUserAccRole> listAccFromDB = smeUserAccRoleRepository.findByCusUserId(user.getCusUserId());
                    boolean isLocked = commonService.isLockedViewAuthority(user, listAccFromDB);
                    log.info("Has user {} been locked view authority by admin? {}", user.getUsername(), isLocked);
                    if (isLocked) {
                        baseResp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.INFO_46);
                        baseResp.setMessage(commonService.getMessage(vn.vnpay.commoninterface.common.Constants.MessageCode.INFO_46, rq.getLang()));
                        return null;
                    }

                    // Validate accountNo
                    boolean isValidAccNo = commonService.validateAccountNo(user, rq.getAccountNo(), rq);
                    if (!isValidAccNo) {
                        log.info("Invalid accountNo: {}", rq.getAccountNo());
                        baseResp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.INFO_42);
                        baseResp.setMessage(
                                commonService.getMessage(
                                        vn.vnpay.commoninterface.common.Constants.MessageCode.INFO_42, rq.getLang()));
                        return null;
                    }

                    // Check quyền truy vấn
                    if (!vn.vnpay.commoninterface.common.Constants.UserRole.ADMIN.equals(user.getRoleType()) && !listAccFromDB.isEmpty()) {
                        Optional<SmeUserAccRole> userAccRoleOpt = listAccFromDB.stream()
                                .filter(e -> "1".equals(e.getIsView()) &&
                                        (rq.getAccountNo().equals(e.getAccNo()) || rq.getAccountNo().equals(e.getAccAlias())))
                                .findFirst();
                        if (!userAccRoleOpt.isPresent()) {
                            log.info("User does not have view authority for this account: {}", rq.getAccountNo());
                            baseResp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.INFO_46);
                            baseResp.setMessage(commonService.getMessage(vn.vnpay.commoninterface.common.Constants.MessageCode.INFO_46, rq.getLang()));
                            return null;
                        }
                    }

                    AccountHistoryBankRequest accHisReq = new AccountHistoryBankRequest();
                    accHisReq.setAccountNo(rq.getAccountNo());
                    accHisReq.setAccountType(rq.getAccountType());
                    accHisReq.setFromDate(rq.getFromDate());
                    accHisReq.setToDate(rq.getToDate());

                    // Kiểm tra tài khoản là alias
                    for (AccountDTO accdto : user.getListAccount()) {
                        if (rq.getAccountNo().equals(accdto.getAccountAlias())) {
                            accHisReq.setAlias(true);
                            break;
                        }
                    }

                    AccountHistoryBankResponse accHisResponse = new AccountHistoryBankResponse();
                    switch (rq.getAccountType()) {
                        case "S":
                        case "D":
                            accHisResponse = coreQueryClient.getDDAccountHistory(accHisReq);
                            break;
                        case "T":
                            accHisResponse = coreQueryClient.getFDAccountHistory(accHisReq);
                            break;
                        case "L":
                            accHisResponse = coreQueryClient.getLNAccountHistory(accHisReq);
                            break;
                        default:
                            log.info("Unsupported account type");
                            break;
                    }
                    if (!accHisResponse.getResponseStatus().getIsSuccess()) {
                        log.info("Failed to fetch account history from bank");
                        baseResp.setCode(accHisResponse.getResponseStatus().getResCode());
                        baseResp.setMessage(accHisResponse.getResponseStatus().getResMessage());
                    }
                    AccountHistoryResponse data = new AccountHistoryResponse();
                    List<AccountHistoryDTO> lstHistory = accHisResponse.getListHistory();
                    //lấy danh sách teller từ bank
                    BaseBankRequest baseBankRequest = new BaseBankRequest();
                    GetListTellerChargebackBankResp getListTellerChargebackBankResp = serviceGWClient.getListTellerChargeback(baseBankRequest);
                    if (!getListTellerChargebackBankResp.getResponseStatus().getIsSuccess()) {
                        baseResp.setCode(getListTellerChargebackBankResp.getResponseStatus().getResCode());
                        baseResp.setMessage(getListTellerChargebackBankResp.getResponseStatus().getResMessage());
                        return null;
                    }
                    //lọc theo teller tra soát
                    lstHistory.forEach(p -> {
                        String tellerSeq = p.getReference();
                        if (StringUtils.isNotBlank(tellerSeq)) {
                            String[] tellerSeqArr = tellerSeq.split("-");
                            p.setTeller(tellerSeqArr[0].trim());
                        }
                    });
                    List<AccountHistoryDTO> lstHistoryAll = lstHistory.stream().filter(
                            p -> getListTellerChargebackBankResp.getListTellerTsol().contains(p.getTeller())
                                    && "D".equals(p.getCd())).collect(Collectors.toList());
                    List<AccountHistoryDTO> lstHistoryResult = new ArrayList<>();
                    //lọc dữ liệu theo user login trả về client
                    if (Constants.UserRole.MAKER.equals(user.getRoleType()) || Constants.UserRole.ALL.equals(user.getRoleType())) {
                        //pilot va live
                        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                        LocalDateTime fromDate = LocalDateTime.parse(rq.getFromDate().concat(" 00:00:00"), formatDateTime).plusDays(-8);
//                        LocalDateTime toDate = LocalDateTime.parse(rq.getToDate().concat(" 00:00:00"), formatDateTime).plusDays(2);
                        //uat va sit
                        LocalDateTime dateNow = LocalDateTime.now();
                        LocalDateTime fromDate = dateNow.plusDays(-8);
                        LocalDateTime toDate = dateNow.plusDays(1);
                        //lấy danh sách data theo DB thời gian truyền vào
                        List<SmeTrans> listTrans = smeTransRepository.findByTranxTimeBetweenAndCifNo(fromDate, toDate, user.getCif());
                        List<SmeTransOffline> listTransOff = smeTransRepositoryOffline.findByTranxTimeBetweenAndCifNo(fromDate, toDate, user.getCif());
                        List<SmeTrans> listTransTwo = modelMapper.map(listTransOff, new TypeToken<List<SmeTrans>>() {
                        }.getType());
                        listTrans.addAll(listTransTwo);
                        List<SmeTrans> listTransFromAcc = listTrans.stream().filter(
                                p -> rq.getAccountNo().equals(p.getFromAcc())
                                        && rq.getUser().equals(p.getCreatedUser())
                        ).collect(Collectors.toList());
                        List<AccountHistoryDTO> lstHistoryTrans = new ArrayList<>();
                        //map data để check
                        for (SmeTrans smeTrans : listTransFromAcc) {
                            TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
                            AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
                            accountHistoryDTO.setPcTime(metaDataDTO.getPcTime());
                            accountHistoryDTO.setTeller(smeTrans.getTeller());
                            accountHistoryDTO.setSeq(metaDataDTO.getSequence());
                            if (metaDataDTO.getHostDate() != null)
                                accountHistoryDTO.setTransactionDate(metaDataDTO.getHostDate().split("T")[0]);
                            lstHistoryTrans.add(accountHistoryDTO);
                        }

                        for (AccountHistoryDTO accHis : lstHistoryAll) {
                            String[] tellerSeqArr = accHis.getReference().split("-");
                            String[] hostdateArr = accHis.getTransactionDate().split("T");
                            String hostDate = hostdateArr[0];
                            String teller = tellerSeqArr[0].trim();
                            String seq = tellerSeqArr[1].trim();
                            String pcTime = accHis.getPcTime().trim();
                            for (AccountHistoryDTO accHisCheck : lstHistoryTrans) {
                                String hostDateCheck = accHisCheck.getTransactionDate();
                                if (StringUtils.isBlank(hostDateCheck)) {
                                    hostDateCheck = hostDate;
                                }
                                if (hostDate.equals(hostDateCheck)
                                        && teller.equals(accHisCheck.getTeller())
                                        && Integer.parseInt(seq) == accHisCheck.getSeq()
                                        && Integer.parseInt(pcTime) == Integer.parseInt(accHisCheck.getPcTime())) {
                                    lstHistoryResult.add(accHis);
                                    break;
                                }

                            }
                        }
                    } else {
                        lstHistoryResult.addAll(lstHistoryAll);
                    }
                    data.setListHistory(lstHistoryResult);
                    return data;
                default:
                    log.info("Invalid user status");
                    baseResp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_100);
                    baseResp.setMessage(
                            commonService.getMessage(
                                    vn.vnpay.commoninterface.common.Constants.MessageCode.USER_100, rq.getLang()));
                    break;
            }
        } else {
            log.info("User not found");
            baseResp.setCode(vn.vnpay.commoninterface.common.Constants.ResCode.USER_404);
            baseResp.setMessage(
                    commonService.getMessage(
                            vn.vnpay.commoninterface.common.Constants.MessageCode.USER_404, rq.getLang()));
        }
        return null;
    }

    /**
     * lấy giao dịch dựa vào các input truyền vào
     *
     * @param smeTransList
     * @param teller
     * @param hostDate
     * @param seq
     * @param pcTime
     * @return
     */
    private SmeTrans getSmeTransFromDb(List<SmeTrans> smeTransList, String teller, String hostDate, String seq, String pcTime) {
        SmeTrans transResult = null;
        for (SmeTrans smeTrans : smeTransList) {
            TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
            String hostdateStr;
            if (StringUtils.isNotBlank(metaDataDTO.getHostDate())) {
                String[] hostdateMetaArr = metaDataDTO.getHostDate().split("T");
                hostdateStr = hostdateMetaArr[0];
            } else {
                hostdateStr = hostDate;
            }
            if (metaDataDTO.getSequence() == Integer.parseInt(seq)
                    && smeTrans.getTeller().equals(teller)
                    && Integer.parseInt(metaDataDTO.getPcTime()) == Integer.parseInt(pcTime)
                    && hostDate.equals(hostdateStr)) {
                transResult = smeTrans;
                break;
            }
        }
        return transResult;
    }

    /**
     * Lưu trạng thái tạo tra soát của giao dịch
     *
     * @param transId
     * @param status
     */
    private void updateStatusCreateTransChargeBack(Long transId, String status) {
        //kiểm tra db ON
        Optional<SmeTrans> smeTransOnOpt = smeTransRepository.findById(transId);
        if (smeTransOnOpt.isPresent()) {
            SmeTrans smeTransOn = smeTransOnOpt.get();
            TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTransOn.getMetadata(), TransactionMetaDataDTO.class);
            metaDataDTO.setStatusCreateChargeback(status);
            smeTransOn.setMetadata(gson.toJson(metaDataDTO));
            smeTransRepository.save(smeTransOn);
        } else {
            //Kiểm tra db off
            Optional<SmeTransOffline> smeTransOffOpt = smeTransRepositoryOffline.findById(transId);
            if (smeTransOffOpt.isPresent()) {
                SmeTransOffline smeTransOff = smeTransOffOpt.get();
                TransactionMetaDataDTO metaDataDTO = gson.fromJson(smeTransOff.getMetadata(), TransactionMetaDataDTO.class);
                metaDataDTO.setStatusCreateChargeback(status);
                smeTransOff.setMetadata(gson.toJson(metaDataDTO));
                smeTransRepositoryOffline.save(smeTransOff);
            }
        }
    }
}