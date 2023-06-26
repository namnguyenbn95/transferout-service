package vn.vnpay.commoninterface.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.common.RestClient;
import vn.vnpay.commoninterface.dto.*;
import vn.vnpay.commoninterface.feignclient.*;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.request.SoftTransConfirmRequest;
import vn.vnpay.commoninterface.request.SoftTransInitRequest;
import vn.vnpay.commoninterface.response.*;
import vn.vnpay.dbinterface.entity.*;
import vn.vnpay.dbinterface.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private Gson gson;

    @Autowired
    RestClient restClient;

    @Autowired
    private Environment env;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisCacheService cache;

    @Autowired
    HardTokenClient hardTokenClient;

    @Autowired
    private SmeMakerServiceRoleRepository smeMakerServiceRoleRepository;

    @Autowired
    private SmeCheckerServiceRoleRepository smeCheckerServiceRoleRepository;

    @Autowired
    SmeCheckRepository smeCheckRepo;

    @Autowired
    private SmeRuleRepository smeRuleRepository;

    @Autowired
    private CoreQueryClient coreQueryClient;

    @Autowired
    private SmeUserDirectTransRepository directTransRepository;

    @Autowired
    private MiscClient miscClient;

    @Autowired
    private DigiCoreTransClient digiCoreTransClient;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    private Tranfer247Client tranfer247Client;

    @Autowired
    private MbServiceRepository mbServiceRepository;

    @Autowired
    private MbPkgPromRepository mbPkgPromRepository;

    @Autowired
    private VCBServiceGWClient vcbServiceGWClient;

    @Autowired
    private SmeSoftOtpClient softOtpClient;

    @Autowired
    private MbServiceDisplayHomeRepository mbServiceDisplayHomeRepository;

    @Autowired
    private DigiCardClient cardClient;

    @Autowired
    private RedisCacheService redisCacheService;

    public String genTranToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    /**
     * Lấy phương thức xác thực giao dịch
     *
     * @param user
     * @return
     */
    public AuthenMethodResponse getAuthenMethod(SmeCustomerUser user) {
        AuthenMethodResponse rp = new AuthenMethodResponse();
        rp.setCode(Constants.MessageCode.INFO_00);
        switch (user.getAuthenMethod()) {
            case Constants.AuthenMethod.HARD_TOKEN:
                rp.setAuthenMethod(Constants.AuthenType.HARD_TOKEN);
                break;
            case Constants.AuthenMethod.SMART_OTP:
                // Kiểm tra user đã kích hoạt Smart OTP chưa
                if ("1".equalsIgnoreCase(user.getActivatedSmartOtp())) {
                    rp.setAuthenMethod(Constants.AuthenType.SMART_OTP);
                } else {
                    log.info("User not active smart otp.");
                    rp.setCode("AUTHEN-METHOD-02");
                }
                break;
            default:
                rp.setCode("AUTHEN-METHOD-03");
                log.info("Authen method is not support.");
                break;
        }
        return rp;
    }

    /**
     * Khởi tạo phương thức xác thực
     *
     * @param user
     * @param rq
     * @param authenType
     * @param fromAcc
     * @param toAcc
     * @param tnxId
     * @param amount
     * @param transToken
     * @param serviceCode
     * @param providerCode
     * @return
     * @throws Exception
     */
    public InitAuthenResponse intAuthen(
            SmeCustomerUser user,
            BaseClientRequest rq,
            String authenType,
            String fromAcc,
            String toAcc,
            Long tnxId,
            String amount,
            String transToken,
            String serviceCode,
            String providerCode,
            String ccy)
            throws Exception {
        InitAuthenResponse rp = new InitAuthenResponse();
        rp.setCode(Constants.ResCode.INFO_00);
        switch (authenType) {
            case Constants.AuthenType.HARD_TOKEN:
                // Kiểm tra trạng thái Hard token
                HardTokenForAuthenRequest tokenForAuthenBankRq = new HardTokenForAuthenRequest();
                tokenForAuthenBankRq.setCif(user.getCifInt());
                tokenForAuthenBankRq.setUsername(user.getUsername());
                tokenForAuthenBankRq.setTokenType(7);
                HardTokenForAuthenResponse tokenForAuthenBankRp =
                        hardTokenClient.getTokenForAuthen(tokenForAuthenBankRq);
                // Thông báo theo kết quả trả về từ bank
                if (!tokenForAuthenBankRp.getResponseStatus().getResCode().equals("0")) {
                    rp.setCode(tokenForAuthenBankRp.getResponseStatus().getResCode());
                    rp.setMessage(
                            commonService.getMessage(
                                    "BANK-" + tokenForAuthenBankRp.getResponseStatus().getResCode(), rq.getLang()));
                    return rp;
                }
                boolean checkToken = false;
                for (HardTokenAuthenMethodDTO item : tokenForAuthenBankRp.getAvailableTokens()) {
                    if (item.getTokenDetail().equals(Strings.nullToEmpty(user.getSerialNumber()))
                            && "3".equals(item.getStatus())) {
                        checkToken = true;
                    }
                }
                // Nếu hard token hợp lệ
                if (checkToken) {
                    // Gọi sang bank sinh mã challenge
                    GetTokenChallengeRequest bankRq = new GetTokenChallengeRequest();
                    bankRq.setCif(Integer.parseInt(user.getCif()));
                    bankRq.setUsername(rq.getUser());
                    bankRq.setTokenType(7);
                    HardTokenTransactionDTO transaction = new HardTokenTransactionDTO();
                    transaction.setTransactionId(String.valueOf(tnxId));
                    transaction.setTransactionAmount(Double.parseDouble(amount));
                    transaction.setTransactionCurrency(ccy);
                    transaction.setTransactionDetail(serviceCode);
                    transaction.setTransactionType(serviceCode);
                    bankRq.setTransaction(transaction);
                    GetTokenChallengeResponse bankRp = hardTokenClient.getTokenChallenge(bankRq);
                    // Thông báo theo kết quả trả về từ bank
                    if (!bankRp.getResponseStatus().getResCode().equals("0")) {
                        rp.setCode(bankRp.getResponseStatus().getResCode());
                        rp.setMessage(
                                commonService.getMessage(
                                        "BANK-" + bankRp.getResponseStatus().getResCode(), rq.getLang()));
                        return rp;
                    }
                    rp.setDataAuthen(bankRp.getChallenge());
                } else {
                    rp.setCode("120");
                    rp.setMessage(commonService.getMessage("BANK-HARD-TOKEN-120", rq.getLang()));
                    return rp;
                }
                break;
            case Constants.AuthenType.SMART_OTP:
                Double da = Double.valueOf(amount);
                SoftTransInitRequest req = new SoftTransInitRequest();
                req.setCifno(user.getCif());
                req.setLang(rq.getLang());
                req.setUsername(rq.getUser());
                req.setMobileNo(user.getMobileOtp());
                req.setFromAccount(fromAcc);
                req.setToAccount(toAcc);
                req.setTransId(tnxId);
                req.setAmount(da.toString());
                req.setTransToken(transToken);
                req.setIp(rq.getClientIP());
                req.setSessionid(rq.getSessionId());
                req.setServiceCode(serviceCode);
                req.setProviderCode(providerCode);
                req.setChannel(rq.getSource().equals("IB") ? "6014" : "6015");
                req.setAttachedRoot(rq.getAttachedRoot());
                req.setAttachedHook(rq.getAttachedHook());
                req.setRequestId(MDC.get("traceId"));
                req.setSignData(req.signData(rq.getUser(), user.getMobileOtp(), req.getRequestTime()));
                SoftTransInitResponse resp = softOtpClient.transactionInit(req);
                if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                    rp.setCode(resp.getCode());
                    rp.setMessage(resp.getMessage());
                    return rp;
                }
                rp.setDataAuthen(resp.getData());

                // Save otpId into cache for confirm step
                if (rq.getSource().equals("IB") && StringUtils.isNotBlank(resp.getOtpId())) {
                    long ttl = Long.parseLong(commonService.getConfig("SESSION_EXPIRE", "15"));
                    cache.hset(Constants.RedisKey.KEY_SOTP_ID, rq.getUser() + tnxId, resp.getOtpId(), ttl, TimeUnit.MINUTES);
                }
                break;
            default:
                rp.setCode("AUTHEN-METHOD-03");
                log.info("Authen method is not support.");
                break;
        }
        return rp;
    }

    /**
     * Xác thực giao dịch
     *
     * @param user
     * @param rq
     * @param authenType
     * @param tnxId
     * @param dataAuthen
     * @return
     * @throws Exception
     */
    public InitAuthenResponse confirmAuthen(
            SmeCustomerUser user,
            BaseClientRequest rq,
            String authenType,
            Long tnxId,
            String dataAuthen,
            String challenge,
            String amount,
            String serviceCode,
            String ccy)
            throws Exception {
        InitAuthenResponse rp = new InitAuthenResponse();
        rp.setCode(Constants.ResCode.INFO_07);
        rp.setMessage(
                commonService.getMessage(
                        vn.vnpay.commoninterface.common.Constants.MessageCode.INFO_07, rq.getLang()));
        switch (authenType) {
            case Constants.AuthenType.PIN:
                boolean isValidPin =
                        CommonUtils.Pin.verify(
                                String.valueOf(user.getCusUserId()), user.getCif(), dataAuthen, user.getPin());
                if (!isValidPin) {
                    int countCheck =
                            commonService.upsertSmeCheck(
                                    user, rq.getIMEI(), Constants.SmeCheckType.CONFIRM_AUTHEN_PIN, rq.getSource());
                    if (countCheck >= Integer.parseInt(commonService.getConfig("MAX_AUTHEN_PIN", "5"))) {
                        log.info("Max pin incorrect.");
                        rp.setCode("08");
                        rp.setMessage(
                                commonService
                                        .getMessage("CONFIRM-AUTHEN-08", rq.getLang())
                                        .replace("{MAX_AUTHEN_PIN}", commonService.getConfig("MAX_AUTHEN_PIN", "5")));
                        return rp;
                    }
                    rp.setCode("07");
                    rp.setMessage(
                            commonService
                                    .getMessage("CONFIRM-AUTHEN-07", rq.getLang())
                                    .replace("{MAX_AUTHEN_PIN}", commonService.getConfig("MAX_AUTHEN_PIN", "5")));
                    return rp;
                }
                // Verify thành công
                rp.setCode(Constants.ResCode.INFO_00);
                // Xóa số lần sai mật khẩu
                smeCheckRepo.deleteByUsernameAndCheckType(
                        user.getUsername(), Constants.SmeCheckType.CONFIRM_AUTHEN_PIN);
                break;
            case Constants.AuthenType.HARD_TOKEN:
                String key = "vcbsme_transaction_hard_token_" + user.getUsername() + "_" + tnxId;
                if (StringUtils.isNotBlank(cache.get(key))) {
                    int count = Integer.parseInt(cache.get(key));
                    if (count >= Integer.parseInt(commonService.getConfig("MAX_FAIL_HARD_TOKEN", "5"))) {
                        log.error("Nhap sai qua so lan quy dinh, cap nhat trang thai sang giao dich loi");
                        smeTransRepository.updateTransStatus(tnxId, Constants.TransStatus.FAIL);
                        // kick out user session
                        redisCacheService.kickoutSession(user.getUsername());
                        rp.setCode("777");
                        rp.setMessage(commonService.getMessage("HARD-TOKEN-777", rq.getLang()));
                        return rp;
                    }
                }

                // Gọi sang bank verify
                HardTokenAuthenRequest bankRq = new HardTokenAuthenRequest();
                bankRq.setCif(Integer.parseInt(user.getCif()));
                bankRq.setUsername(user.getUsername());
                bankRq.setUserBranch("");
                bankRq.setTokenType(7);
                bankRq.setOtp(dataAuthen);
                bankRq.setChallenge(challenge);
                HardTokenTransactionDTO transaction = new HardTokenTransactionDTO();
                transaction.setTransactionId(String.valueOf(tnxId));
                transaction.setTransactionAmount(Double.parseDouble(amount));
                transaction.setTransactionCurrency(ccy);
                transaction.setTransactionDetail(serviceCode);
                transaction.setTransactionType(serviceCode);
                bankRq.setTransaction(transaction);
                HardTokenAuthenResponse bankRp = hardTokenClient.authenHardToken(bankRq);
                if (!bankRp.getResponseStatus().getIsSuccess()) {
                    Integer count = !Strings.isNullOrEmpty(cache.get(key)) ? Integer.parseInt(cache.get(key)) + 1 : 1;
                    if (count >= Integer.parseInt(commonService.getConfig("MAX_FAIL_HARD_TOKEN", "5"))) {
                        cache.set(key, String.valueOf(count), 15, TimeUnit.MINUTES);
                        rp.setCode("777");
                        rp.setMessage(commonService.getMessage("HARD-TOKEN-777", rq.getLang()));
                        return rp;
                    }
                    cache.set(key, String.valueOf(count), 15, TimeUnit.MINUTES);
                    rp.setCode(Constants.ResCode.INFO_07);
                    rp.setMessage(commonService.getMessage(vn.vnpay.commoninterface.common.Constants.MessageCode.INFO_07, rq.getLang()));
                    return rp;
                }
                // Verify thành công
                cache.delete(key);  //invalidate cache confirm fail hard-token
                rp.setCode(Constants.ResCode.INFO_00);
                break;
            case Constants.AuthenType.SMART_OTP:
                SoftTransConfirmRequest req = new SoftTransConfirmRequest();
                // Fetch otpId from cache
                if (rq.getSource().equals("IB")) {
                    String otpId = cache.getHSet(Constants.RedisKey.KEY_SOTP_ID, user.getUsername() + tnxId);
                    if (StringUtils.isBlank(otpId)) {
                        log.info("Not found otpId from cache!!!");
                        rp.setCode(Constants.ResCode.ERROR_96);
                        rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                        return rp;
                    }
                    req.setOtpId(otpId);
                }

                req.setCifno(user.getCif());
                req.setLang(rq.getLang());
                req.setUsername(rq.getUser());
                req.setMobileNo(user.getMobileOtp());
                req.setData(dataAuthen);
                req.setTransId(String.valueOf(tnxId));
                req.setDeviceType(rq.getOS());
                req.setDevicemodel(rq.getPM());
                req.setDeviceid(rq.getIMEI());
                req.setChannel(rq.getSource().equals("IB") ? "6014" : "6015");
                req.setSessionid(rq.getSessionId());
                req.setRequestId(MDC.get("traceId"));
                req.setSignData(req.signData(rq.getUser(), user.getMobileOtp(), req.getRequestTime()));
                SoftTransInitResponse resp = softOtpClient.transactionConfirm(req);
                if (!Constants.ResCode.INFO_00.equals(resp.getCode())) {
                    rp.setCode(resp.getCode());
                    rp.setMessage(resp.getMessage());
                    return rp;
                }
                // Verify thành công
                rp.setCode(Constants.ResCode.INFO_00);

                // Xóa cache
                if (rq.getSource().equals("IB")) {
                    cache.deleteHset(Constants.RedisKey.KEY_SOTP_ID, user.getUsername() + tnxId);
                }
                break;
            default:
                rp.setCode("07");
                log.info("Authen method is not support.");
                break;
        }
        return rp;
    }

    /**
     * Valid giao dich thuoc user
     *
     * @param req
     * @return : resp.data -> cache đã được lưu ở bước khỏi tạo
     */
    public BaseClientResponse validTxn(BaseConfirmRq req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        // Kiem tra giao dich co ton tai voi user
        String result = cache.getTxn(req);
        if (result == null) {
            resp.setCode("TXN-TK-EX");
            resp.setMessage(commonService.getMessage("TXN-TK-EX", req.getLang()));
            return resp;
        }
        // Khơi tạo xử lý- trùng giao dịch. Không cho phép đồng thời 2 giao dịch vào cùng lúc
        String key =
                Constants.CACHE_PREFIX
                        + Constants.RedisKey.KEY_SEC_TXN
                        + req.getUser()
                        + req.getSessionId()
                        + req.getTranToken();
        Long max =
                cache.increment(
                        key, Long.valueOf(commonService.getConfig("SESSION_EXPIRE", "10")), TimeUnit.MINUTES);
        if (max > 2) {
            log.info("check max cache:" + max);
            resp.setCode("TXN-DP");
            resp.setMessage(commonService.getMessage("TXN-DP", req.getLang()));
            return resp;
        }
        // resp.setData(result);
        // resp = gson.fromJson(result, BaseClientResponse.class);
        return resp;
    }

    /**
     * Xác nhận OTP và check bảo mật duplicate giao dich
     *
     * @param user
     * @param rq
     * @param authenType
     * @param tnxId
     * @param dataAuthen
     * @return
     */
    public InitAuthenResponse confirmAuthenTxn(
            SmeCustomerUser user,
            BaseClientRequest rq,
            String authenType,
            Long tnxId,
            String dataAuthen,
            String tranToken,
            String challenge,
            String amount,
            String serviceCode,
            String ccy) {
        InitAuthenResponse rp = new InitAuthenResponse();
        try {
            rp = confirmAuthen(user, rq, authenType, tnxId, dataAuthen, challenge, amount, serviceCode, ccy);

        } catch (Exception e) {
            log.info("Ex confirmAuthenTxn:", e);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } finally {
            // Xử lý- trùng giao dịch -
            if (!Constants.ResCode.INFO_00.equals(rp.getCode())) {
                String key =
                        Constants.CACHE_PREFIX
                                + Constants.RedisKey.KEY_SEC_TXN
                                + rq.getUser()
                                + rq.getSessionId()
                                + tranToken;
                cache.decrement(key);
            }
        }
        return rp;
    }

    public BaseClientResponse validateTransAuthorityForAccount(
            BaseClientResponse rp, SmeCustomerUser user, String debitAccNo, String serviceCode, String lang) {
        // Kiểm tra role type. Nếu là Admin thì được phép lập lệnh
        if (Constants.UserRole.ADMIN.equals(user.getRoleType())) {
            return rp;
        }
        String serviceCodeCheck;
        if (Constants.ServiceCode.FAST_TRANS_BILATERAL_ACCNO.equals(serviceCode))
            serviceCodeCheck = Constants.ServiceCode.FAST_TRANS_VIA_ACCNO;
        else
            serviceCodeCheck = serviceCode;


        // Kiểm tra quyền giao dịch
        List<SmeMakerServiceRole> listMakerSerRole = smeMakerServiceRoleRepository.findByCusUsernameAndAccNoNotNull(user.getUsername());
        if (listMakerSerRole.isEmpty()) {
            // Admin chưa phân quyền -> Mặc định được giao dịch
            log.info("Admin chua phan quyen.");
            return rp;
        }

        // Check khóa quyền
        boolean isLocked = true;
        for (SmeMakerServiceRole entity : listMakerSerRole) {
            if ("1".equals(entity.getStatus())) {
                isLocked = false;
                break;
            }
        }
        if (isLocked) {
            rp.setCode(Constants.ResCode.INFO_21);
            String message = commonService.getMessage(Constants.MessageCode.INFO_21, lang);

            Optional<MbService> serviceOpt = mbServiceRepository.findByServiceCode(serviceCodeCheck);
            if (serviceOpt.isPresent()) {
                if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                    message = message.replace("$1", serviceOpt.get().getServiceNameEn());
                } else {
                    message = message.replace("$1", serviceOpt.get().getServiceName());
                }
            }
            rp.setMessage(message);
            return rp;
        }

        Optional<SmeMakerServiceRole> debitAccRoleOpt = listMakerSerRole.stream().filter(entity -> "1".equals(entity.getIsTrans())
                && "1".equals(entity.getStatus())
                && (debitAccNo.equals(entity.getAccNo())
                || debitAccNo.equals(entity.getAccAlias()))).findFirst();

        if (!debitAccRoleOpt.isPresent()) {
            log.info("Tài khoản nguồn không hợp lệ -> Kiểm tra quyền giao dịch.");
            rp.setCode(Constants.ResCode.INFO_20);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_20, lang));
            return rp;
        }
        return rp;
    }

    /**
     * Kiểm tra Mã truy cập có được phép thao tác dịch vụ & loại giao dịch hay không (lập lệnh hoặc
     * duyệt lệnh)
     *
     * @param rp
     * @param user
     * @param serviceCode
     * @param txnStep
     * @param lang
     * @param maker:      Thông tin người lập lệnh. Cần truyền khi txnStep = 2 (Duyệt lệnh)
     * @return
     */
    public BaseClientResponse validateUserAndServiceCode(
            BaseClientResponse rp,
            SmeCustomerUser user,
            String serviceCode,
            String txnStep,
            String lang,
            String maker) {
        log.info(
                "Validate trans authority for user {}, service code {}, and step {}",
                user.getUsername(),
                serviceCode,
                txnStep);
        log.info("Confirm type: {}", user.getConfirmType());
        log.info("Role type: {}", user.getRoleType());
        if (Constants.UserRole.ADMIN.equals(user.getRoleType())) {
            return rp;
        }
        String serviceCodeCheck;
        if (Constants.ServiceCode.FAST_TRANS_BILATERAL_ACCNO.equals(serviceCode))
            serviceCodeCheck = Constants.ServiceCode.FAST_TRANS_VIA_ACCNO;
        else
            serviceCodeCheck = serviceCode;

        switch (txnStep) {
            case "1": // Lập lệnh
                // Kiểm tra role type có được quyền thực hiện giao dịch hay không
                boolean isValidRoleType = false;
                if ("1".equals(user.getConfirmType())) {
                    if (Arrays.asList(Constants.UserRole.ALL, Constants.UserRole.ADMIN)
                            .contains(user.getRoleType())) {
                        isValidRoleType = true;
                    }
                } else {
                    if (Arrays.asList(Constants.UserRole.MAKER, Constants.UserRole.ADMIN)
                            .contains(user.getRoleType())) {
                        isValidRoleType = true;
                    }
                }
                log.info("Is valid role type? {}", isValidRoleType);
                if (!isValidRoleType) {
                    rp.setCode(Constants.ResCode.USER_102);
                    rp.setMessage(commonService.getMessage(Constants.MessageCode.USER_102, lang));
                    return rp;
                }

                // Kiểm tra quyền thực hiện dịch vụ đối với admin 2 cấp
                if ("2".equals(user.getConfirmType()) && Constants.UserRole.ADMIN.equals(user.getRoleType())) {
                    List<MbServiceDisplayHome> listServiceDisplayHome = mbServiceDisplayHomeRepository.findAll();
                    List<MbServiceDisplayHome> listAllowedService = listServiceDisplayHome.stream().filter(e -> {
                        if (StringUtils.isNotBlank(e.getServiceCodes())) {
                            if (Arrays.asList(e.getServiceCodes().split(",")).contains(serviceCodeCheck)) {
                                return true;
                            }
                        }
                        if (StringUtils.isNotBlank(e.getBillServiceCode())) {
                            if (Arrays.asList(e.getBillServiceCode().split(",")).contains(serviceCodeCheck)) {
                                return true;
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());

                    List<MbServiceDisplayHome> listAllowedObject = listAllowedService.stream().filter(e -> {
                        if ((e.getObject().contains("All") || e.getObject().contains("Admin")) && (e.getApproveModel().equals("0") || e.getApproveModel().equals("2"))) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());

                    if (listAllowedObject.isEmpty()) {
                        log.info("Check admin 2 cap ---> listAllowedObject is empty");
                        rp.setCode(Constants.ResCode.INFO_21);
                        String message = commonService.getMessage(Constants.MessageCode.INFO_21, lang);

                        Optional<MbService> serviceOpt = mbServiceRepository.findByServiceCode(serviceCodeCheck);
                        if (serviceOpt.isPresent()) {
                            if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                                message = message.replace("$1", serviceOpt.get().getServiceNameEn());
                            } else {
                                message = message.replace("$1", serviceOpt.get().getServiceName());
                            }
                        }
                        rp.setMessage(message);
                        return rp;
                    }
                }

                // Kiểm tra Mã truy cập có được quyền thực hiện dịch vụ hay không
                List<SmeMakerServiceRole> listMkSerRole =
                        smeMakerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                user.getUsername());
                boolean isValidUserService = false;
                if (listMkSerRole.isEmpty()) {
                    log.info("List maker service role is empty ----> Allowed trans execution by default");
                    isValidUserService = true;
                } else {
                    Optional<SmeMakerServiceRole> mkSerRoleOpt =
                            listMkSerRole.stream()
                                    .filter(
                                            e ->
                                                    e.getCusUserId() == user.getCusUserId()
                                                            && serviceCodeCheck.equals(e.getServiceCode())
                                                            && "1".equals(e.getIsTrans())
                                                            && "1".equals(e.getStatus()))
                                    .findFirst();
                    if (mkSerRoleOpt.isPresent()) {
                        isValidUserService = true;
                    }
                }
                log.info("Is valid user-service? {}", isValidUserService);
                if (!isValidUserService) {
                    rp.setCode(Constants.ResCode.INFO_21);
                    String message = commonService.getMessage(Constants.MessageCode.INFO_21, lang);

                    Optional<MbService> serviceOpt = mbServiceRepository.findByServiceCode(serviceCodeCheck);
                    if (serviceOpt.isPresent()) {
                        if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                            message = message.replace("$1", serviceOpt.get().getServiceNameEn());
                        } else {
                            message = message.replace("$1", serviceOpt.get().getServiceName());
                        }
                    }
                    rp.setMessage(message);
                    return rp;
                }
                break;
            case "2": // Duyệt lệnh
                // Kiểm tra role type có được quyền thực hiện giao dịch hay không
                isValidRoleType = false;
                if ("2".equals(user.getConfirmType())) {
                    if (Arrays.asList(Constants.UserRole.CHECKER, Constants.UserRole.ADMIN)
                            .contains(user.getRoleType())) {
                        isValidRoleType = true;
                    }
                }
                log.info("Is valid role type? {}", isValidRoleType);
                if (!isValidRoleType) {
                    rp.setCode(Constants.ResCode.USER_102);
                    rp.setMessage(commonService.getMessage(Constants.MessageCode.USER_102, lang));
                    return rp;
                }

                // Kiểm tra Mã truy cập có được quyền thực hiện dịch vụ hay không
                List<SmeCheckerServiceRole> listCkSerRole =
                        smeCheckerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(
                                user.getUsername());
                isValidUserService = false;
                if (listCkSerRole.isEmpty()) {
                    log.info("List checker service role is empty ----> Allowed trans execution by default");
                    isValidUserService = true;
                } else {
                    Optional<SmeCheckerServiceRole> ckSerRoleOpt =
                            listCkSerRole.stream()
                                    .filter(
                                            e ->
                                                    e.getCusUserId() == user.getCusUserId()
                                                            && serviceCodeCheck.equals(e.getServiceCode())
                                                            && e.getMakerUser().equals(maker)
                                                            && "1".equals(e.getIsTrans())
                                                            && "1".equals(e.getStatus()))
                                    .findFirst();
                    if (ckSerRoleOpt.isPresent()) {
                        isValidUserService = true;
                    }
                }
                log.info("Is valid user-service? {}", isValidUserService);
                if (!isValidUserService) {
                    rp.setCode(Constants.ResCode.INFO_40);
                    String message = commonService.getMessage(Constants.MessageCode.INFO_40, lang);

                    Optional<MbService> serviceOpt = mbServiceRepository.findByServiceCode(serviceCodeCheck);
                    if (serviceOpt.isPresent()) {
                        if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                            message = message.replace("$1", serviceOpt.get().getServiceNameEn());
                        } else {
                            message = message.replace("$1", serviceOpt.get().getServiceName());
                        }
                    }
                    rp.setMessage(message);
                    return rp;
                }
                break;
        }
        return rp;
    }

    public BaseClientResponse validateAccountProduct(
            BaseClientResponse baseResp,
            String serviceCode,
            String debitProduct,
            String creditProduct,
            String lang) {
        log.info("Validate account product: debit={}; credit={}", debitProduct, creditProduct);

        // Lấy thông tin rule mã sản phẩm
        Optional<SmeRule> ruleOpt = smeRuleRepository.findByServiceCodeAndStatus(serviceCode, "1");
        if (!ruleOpt.isPresent()) {
            log.info("Account product rule not found by service code {}", serviceCode);
            baseResp.setCode(Constants.ResCode.INFO_22);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_22, lang));
            return baseResp;
        }

        SmeRule smeRule = ruleOpt.get();
        String creditProductAllowed;
        if (Strings.nullToEmpty(smeRule.getDebitAccountExt()).contains("(" + debitProduct + ")")) {
            creditProductAllowed =
                    Strings.nullToEmpty(smeRule.getCreditAccount())
                            + Strings.nullToEmpty(smeRule.getCreditAccountExt());
        } else if (Strings.nullToEmpty(smeRule.getDebitAccount()).contains("(" + debitProduct + ")")) {
            creditProductAllowed = Strings.nullToEmpty(smeRule.getCreditAccount());
        } else {
            log.info("Invalid debit product.");
            baseResp.setCode(Constants.ResCode.INFO_22);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_22, lang));
            return baseResp;
        }
        log.info("creditProductAllowed: {}", creditProductAllowed);

        if (Strings.isNullOrEmpty(
                creditProduct)) { // Chuyển khoản ngoài vcb -> không cần check rule credit
            return baseResp;
        }

        if (!creditProductAllowed.contains("(" + creditProduct + ")")) {
            log.info("creditProductAllowed does not contain credit product code");
            baseResp.setCode(Constants.ResCode.INFO_36);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_36, lang));
            return baseResp;
        }
        return baseResp;
    }

    /**
     * Kiểm tra giao dịch đi ngay hay phải qua bước duyệt lệnh
     *
     * @param roleType
     * @param username
     * @param serviceCode
     * @param accountNo
     * @return true: Giao dịch đi ngay; false: Giao dịch cần qua bước duyệt
     */
    public boolean isExecTrans(String roleType, String username, String serviceCode, String accountNo) {
        log.info("isExecTrans roleType {} username {} serviceCode {} accountNo {}",
                roleType, username, serviceCode, accountNo);
        if (Arrays.asList(Constants.UserRole.ALL, Constants.UserRole.ADMIN).contains(roleType)) {
            return true;
        }
        Optional<SmeUserDirectTrans> directTransOpt =
                directTransRepository.checkDirectTrans(username, serviceCode, accountNo);
        if (directTransOpt.isPresent()) {
            return true;
        }
        return false;
    }

    public BaseClientResponse checkDebitAccountStatus(
            BaseClientResponse baseResp,
            String accNo,
            String accType,
            boolean isAlias,
            boolean isDebit,
            String lang) {
        log.info("Account Status Inquiry: isDebit = {}", isDebit);
        AccountStatusInquiryBankRequest accSttInquiryReq =
                AccountStatusInquiryBankRequest.builder()
                        .accountNo(accNo)
                        .accountType(accType)
                        .isAlias(isAlias)
                        .build();
        AccountStatusInquiryBankResponse accSttResp =
                coreQueryClient.accountStatusInquiry(accSttInquiryReq);
        if (!accSttResp.getResponseStatus().getIsSuccess()) {
            log.info("Failed to get debit account status");
            baseResp.setCode(accSttResp.getResponseStatus().getResCode());
            baseResp.setMessage(accSttResp.getResponseStatus().getResMessage());
            return baseResp;
        }
        log.info("Join status: {}", accSttResp.getJointSts());
        if (isDebit && "Y".equalsIgnoreCase(accSttResp.getJointSts())) {
            baseResp.setCode(Constants.ResCode.INFO_29);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_29, lang));
            return baseResp;
        }

        if (isDebit) {
            log.info("Credit status: {}", accSttResp.getCreditSts());
            if ("Y".equalsIgnoreCase(accSttResp.getCreditSts())
                    || "Y".equalsIgnoreCase(accSttResp.getPostingSts())
                    || "Y".equalsIgnoreCase(accSttResp.getFdBlockedSts())
                    || "Y".equalsIgnoreCase(accSttResp.getFdLostSts())
                    || "Y".equalsIgnoreCase(accSttResp.getFdUnderLienSts())) {
                // Tài khoản debit chỉ được phép ghi có -> không được phép trích nợ -> trả lỗi
                baseResp.setCode(Constants.ResCode.INFO_23);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_23, lang));
                return baseResp;
            }
        } else {
            log.info("Debit status: {}", accSttResp.getDebitSts());
            if ("Y".equalsIgnoreCase(accSttResp.getDebitSts())
                    || "Y".equalsIgnoreCase(accSttResp.getPostingSts())) {
                // Tài khoản credit chỉ được phép trích nợ -> không được phép ghi có -> trả lỗi
                baseResp.setCode(Constants.ResCode.INFO_30);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_30, lang));
                return baseResp;
            }
        }
        return baseResp;
    }

    public BaseClientResponse execTransIn(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) throws Exception {
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        MbService mbService = mbServiceRepository.findByServiceCode(smeTrans.getTranxType()).get();
        String teller = mbService.getTellerId();
        int seq = (int) (smeTrans.getId() % 100000);
        metadata.setTellerId(teller);
        metadata.setSequence(seq);
        smeTrans.setTeller(teller);
        smeTrans.setMetadata(gson.toJson(metadata));

        switch (smeTrans.getTranxType()) {
            case Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE:
            case Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED:
                // Đăng ký giao dịch tương lai/định kỳ sang bank
                metadata.getFutureTransData().setTransId(String.valueOf(smeTrans.getId()));
                metadata.getFutureTransData().setBatchId(String.valueOf(smeTrans.getId()));
                metadata.getFutureTransData().setCheckerId(req.getUser());
                metadata.getFutureTransData().setRemark(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())));
                smeTrans.setMetadata(gson.toJson(metadata));

                FutureTransDataDTO futureData = metadata.getFutureTransData();
                futureData.setTellerId(teller);

                String code = "0199";
                try {
                    RegisterFutureTransBankRequest bankReq = RegisterFutureTransBankRequest.builder()
                            .transData(futureData)
                            .build();
                    RegisterFutureTransBankResponse bankResp = miscClient.registerFutureTrans(bankReq);
                    smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
                    smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());
                    if (bankResp.getResponseStatus().getIsSuccess()) {
                        smeTrans.setStatus(Constants.TransStatus.SUCCESS);
                        smeTrans.setTranxNote("Duyệt lệnh thành công");
                    } else {
                        // Cập nhật trạng thái giao dịch
                        if (bankResp.getResponseStatus().getIsTimeout()) {
                            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                            smeTrans.setTranxNote("Giao dịch timed out");
                        } else {
                            smeTrans.setStatus(Constants.TransStatus.FAIL);
                            smeTrans.setTranxNote("Giao dịch lỗi");
                            code = bankResp.getResponseStatus().getResCode();
                        }
                        smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
                        smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());
                        baseResp.setCode(code);
                        baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                    }
                } catch (RetryableException ex) {
                    log.info("Error: ", ex);
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                    baseResp.setCode(code);
                    baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                } catch (Exception ex) {
                    log.info("Error: ", ex);
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                    baseResp.setCode(code);
                    baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);
                break;
            default:
                String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
                metadata.setPcTime(pcTime);
                TransferInBankRequest transfer = TransferInBankRequest.builder()
                        .content(smeTrans.getTranxContent())
                        .creditAccount(metadata.getCreditAccount())
                        .debitAccount(metadata.getDebitAccount())
                        .fee(metadata.getFee())
                        .originAmount(smeTrans.getAmount())
                        .amountVND(smeTrans.getTotalAmount())
                        .originCurrency(smeTrans.getCcy())
                        .pcTime(pcTime)
                        .remark(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())))
                        .sequence(seq)
                        .tellerBranch(6800)
                        .tellerId(teller)
                        .txnType(Constants.TransType.TRANSFER)
                        .advice(metadata.getCreditAdviceFlag())
                        .build();

                // Gọi bank hạch toán
                code = "0199";
                try {
                    TransferInBankResponse transferInBankResponse = digiCoreTransClient.transferIn(transfer);
                    metadata.setHostDate(transferInBankResponse.getHostDate());
                    smeTrans.setTranxRefno(transferInBankResponse.getMsgID());
                    smeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                    smeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                    smeTrans.setMetadata(gson.toJson(metadata));

                    // Update trạng thái giao dich
                    smeTrans.setStatus(Constants.TransStatus.SUCCESS);
                    smeTrans.setTranxNote("Thành công");

                    if (!transferInBankResponse.getResponseStatus().getIsSuccess()) {
                        // Update trạng thái giao dich
                        if (transferInBankResponse.getResponseStatus().getIsTimeout()) {
                            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                            smeTrans.setTranxNote("Giao dịch timed out");
                        } else {
                            smeTrans.setStatus(Constants.TransStatus.FAIL);
                            smeTrans.setTranxNote("Giao dịch lỗi");
                            code = transferInBankResponse.getResponseStatus().getResCode();
                        }
                        cache.pushTxn(req, req.getTranToken(), smeTrans);
                        smeTransRepository.save(smeTrans);
                        baseResp.setCode(code);
                        baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                        return baseResp;
                    }

                    // Ghi log tài khoản ảo
                    log.info("Log VR account transaction? {}", metadata.isCreditVRA());
                    if (metadata.isCreditVRA()) {
                        try {
                            String vdate = CommonUtils.TimeUtils.format("yyyy-MM-dd'T'HH:mm:ss", new Date());
                            VRALogTransactionBankRequest logVRABankReq =
                                    VRALogTransactionBankRequest.builder()
                                            .cif(metadata.getCreditAccount().getCif())
                                            .ddAccount(metadata.getRealAccountNumber())
                                            .ddAccountName(metadata.getRealAccountName())
                                            .vrAccount(metadata.getVrAccount())
                                            .vrAccountName(metadata.getVaName())
                                            .amount(smeTrans.getAmount())
                                            .currency(smeTrans.getCcy())
                                            .tellerID(teller)
                                            .sequence(seq)
                                            .branch(6800)
                                            .hostDate(transferInBankResponse.getHostDate())
                                            .vdate(vdate)
                                            .status(metadata.getVaStatus())
                                            .ref(StringUtils.EMPTY)
                                            .chrgCod(metadata.getFee().getChargeType())
                                            .chrgAmt(metadata.getFee().getOriginAmount() + metadata.getFee().getOriginVatAmount())
                                            .chrgCurr(metadata.getFee().getCurrency())
                                            .rate(new BigDecimal(metadata.getCreditAccount().getRate()).doubleValue())
                                            .dbAcct(metadata.getDebitAccount().getAccountNo())
                                            .dbName(metadata.getDebitAccount().getAccountHolderName())
                                            .benBank("970436")
                                            .rmInfo(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())))
                                            .createTime(vdate)
                                            .build();
                            miscClient.logVRATrans(logVRABankReq);
                        } catch (Exception e) {
                            log.info("Error: ", e);
                        }
                    }
                } catch (RetryableException ex) {
                    log.info("Error: ", ex);
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                    baseResp.setCode(code);
                    baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                } catch (Exception ex) {
                    log.info("Error: ", ex);
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                    baseResp.setCode(code);
                    baseResp.setMessage(commonService.getMessage("TRANSFER-IN-" + code, req.getLang()));
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);
                break;
        }
        return baseResp;
    }

    public BaseClientResponse execTransOutIBPS(BaseClientResponse baseResp,
                                               SmeTrans cachedSmeTrans,
                                               BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        metaData.setTellerId(teller);
        metaData.setSequence(seq);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        switch (cachedSmeTrans.getTranxType()) {
            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE:
            case Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED:
                FutureTransDataDTO futureTransData =
                        FutureTransDataDTO.builder()
                                .debitAccount(metaData.getDebitAccount())
                                .creditAccount(metaData.getCreditAccount())
                                .fee(metaData.getFee())
                                .amount(metaData.getOriginAmount())
                                .currency(metaData.getDebitAccount().getCurrency())
                                .tellerBranch("06800")
                                .tellerId(teller)
                                .cronJob(metaData.getCronJob())
                                .transType(
                                        cachedSmeTrans
                                                .getTranxType()
                                                .equals(Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE)
                                                ? "FUTUREDATE"
                                                : "RECURRING")
                                .transId(String.valueOf(cachedSmeTrans.getId()))
                                .batchId(String.valueOf(cachedSmeTrans.getId()))
                                .makerId(metaData.getFutureTransData().getMakerId())
                                .checkerId(rq.getUser())
                                .content(cachedSmeTrans.getTranxContent())
                                .remark(
                                        cachedSmeTrans
                                                .getTranxRemark()
                                                .replace("$1", String.valueOf(cachedSmeTrans.getId())))
                                .creditChannel("2")
                                .build();
                RegisterFutureTransBankRequest bankReq =
                        RegisterFutureTransBankRequest.builder().transData(futureTransData).build();
                try {
                    RegisterFutureTransBankResponse bankResp = miscClient.registerFutureTrans(bankReq);
                    cachedSmeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
                    cachedSmeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());
                    if (!bankResp.getResponseStatus().getResCode().equals("0")) {
                        // Cập nhật trạng thái giao dịch
                        if (bankResp.getResponseStatus().getIsTimeout()) {
                            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                            cachedSmeTrans.setTranxNote("Giao dịch timed out");
                            baseResp.setCode("0169");
                            baseResp.setMessage(commonService.getMessage("IBPS-0169", rq.getLang()));
                        } else {
                            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                            baseResp.setCode(bankResp.getResponseStatus().getResCode());
                            baseResp.setMessage(commonService.getMessage("IBPS-" + baseResp.getCode(), rq.getLang()));
                        }
                        smeTransRepository.save(cachedSmeTrans);
                    } else {
                        cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                        cachedSmeTrans.setTranxNote("Duyệt lệnh thành công");
                        smeTransRepository.save(cachedSmeTrans);
                        BaseTransactionResponse data = new BaseTransactionResponse();
                        data.setTranDate(CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                        data.setIsExecTrans("1");
                        baseResp.setData(data);
                    }
                } catch (RetryableException ex) {
                    log.info("Error: ", ex);
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                } catch (Exception ex) {
                    log.info("Error: ", ex);
                    cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                    cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                }
                cache.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
                smeTransRepository.save(cachedSmeTrans);
                break;
            default:
                String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
                metaData.setPcTime(pcTime);
                TransferOutBankRequest transfer =
                        TransferOutBankRequest.builder()
                                .content(cachedSmeTrans.getTranxContent())
                                .creditAccount(metaData.getCreditAccount())
                                .debitAccount(metaData.getDebitAccount())
                                .fee(metaData.getFee())
                                .amountVND(metaData.getAmountVND())
                                .originAmount(metaData.getOriginAmount())
                                .originCurrency(metaData.getDebitAccount().getCurrency())
                                .pcTime(pcTime)
                                .sequence(seq)
                                .traceId(seq)
                                .tellerBranch(6800)
                                .tellerId(teller)
                                .content(cachedSmeTrans.getTranxContent())
                                .remark(
                                        cachedSmeTrans
                                                .getTranxRemark()
                                                .replace("$1", String.valueOf(cachedSmeTrans.getId())))
                                .TxnId(String.valueOf(cachedSmeTrans.getId()))
                                .isTranViaCard(metaData.isTranViaCard())
                                .build();
                try {
                    TransferOutBankResponse transferInBankResponse = digiCoreTransClient.transferOut(transfer);
                    // Cap nhat chi tiet giao dich
                    if (!transferInBankResponse.getResponseStatus().getResCode().equals("0")) {
                        // Update trạng thái giao dich
                        if (transferInBankResponse.getResponseStatus().getIsTimeout()) {
                            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                            cachedSmeTrans.setTranxNote("Giao dịch timed out");
                            baseResp.setCode("0169");
                            baseResp.setMessage(commonService.getMessage("IBPS-0169", rq.getLang()));
                        } else {
                            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                            baseResp.setCode(transferInBankResponse.getResponseStatus().getResCode());
                            baseResp.setMessage(commonService.getMessage("IBPS-" + baseResp.getCode(), rq.getLang()));
                        }
                        RmDataDTO rmDataDTO = transferInBankResponse.getRmData();
                        if (rmDataDTO != null)
                            metaData.setRmFwBranch(rmDataDTO.getRmForwardBranch()); //branch gửi điện lưu phục vụ đối soát
                        cachedSmeTrans.setMetadata(gson.toJson(metaData));
                        cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                        cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                        smeTransRepository.save(cachedSmeTrans);
                    } else {
                        // Update trạng thái giao dich
                        cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                        cachedSmeTrans.setTranxNote("Thành công");
                        cachedSmeTrans.setTranxRefno(transferInBankResponse.getMsgID());
                        cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                        cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                        RmDataDTO rmDataDTO = transferInBankResponse.getRmData();
                        if (rmDataDTO != null)
                            metaData.setRmFwBranch(rmDataDTO.getRmForwardBranch()); //branch gửi điện lưu phục vụ đối soát
                        metaData.setHostDate(transferInBankResponse.getHostDate());
                        cachedSmeTrans.setMetadata(gson.toJson(metaData));
                        smeTransRepository.save(cachedSmeTrans);
                        BaseTransactionResponse data = new BaseTransactionResponse();
                        data.setTranDate(CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                        data.setTranxId(String.valueOf(transferInBankResponse.getMsgID()));
                        data.setIsExecTrans("1");
                        baseResp.setData(data);
                    }
                } catch (RetryableException ex) {
                    log.info("Error: ", ex);
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                } catch (Exception ex) {
                    log.info("Error: ", ex);
                    cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                    cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                }
                cache.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
                smeTransRepository.save(cachedSmeTrans);
        }
        return baseResp;
    }

    public BaseClientResponse execTransfer247ViaAcc(
            BaseClientResponse baseResp,
            SmeTrans cachedSmeTrans,
            String hostDate,
            BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        metaData.setTellerId(teller);
        metaData.setSequence(seq);
        metaData.setHostDate(hostDate);
        metaData.setPcTime(pcTime);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        //build request
        RequestContentAcc247Payment requestContentAcc247Payment = RequestContentAcc247Payment.builder()
                .creditInfo(metaData.getCreditAccount())
                .debitInfo(metaData.getDebitAccount())
                .build();
        RequestContentAcc247TransInfo transInfo = RequestContentAcc247TransInfo.builder()
                .adviceRoute(metaData.getAdviceRoute())
                .approvalTime(cachedSmeTrans.getApprovedDate() != null ? DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getApprovedDate()) : DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getTranxTime()))
                .createdTime(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getTranxTime()))
                .creditAmount(metaData.getCreditAccount().getOriginAmount())
                .creditCurrency(metaData.getCreditAccount().getCurrency())
                .creditLCEAmount(metaData.getCreditAccount().getAmountVND())
                .debitAmount(metaData.getDebitAccount().getOriginAmount())
                .debitCurrency(metaData.getDebitAccount().getCurrency())
                .debitLCEAmount(metaData.getDebitAccount().getAmountVND())
                .feeAmount(metaData.getFee().getOriginAmount())
                .feeCurrency(metaData.getFee().getCurrency())
                .feeVND(metaData.getFee().getAmountVND())
                .remark(cachedSmeTrans
                        .getTranxRemark()
                        .replace("$1", String.valueOf(cachedSmeTrans.getId())))
                .tranferContent(cachedSmeTrans.getTranxContent())
                .tranferAmount(metaData.getCreditAccount().getAmountVND())
                .tranferBranch(metaData.getDebitAccount().getBranch())
                .tranferCurrency(metaData.getCreditAccount().getCurrency())
                .tranferRate(Double.parseDouble(metaData.getDebitAccount().getRate()))
                .vatAmount(metaData.getFee().getOriginVatAmount())
                .vatCurrency(metaData.getFee().getCurrency())
                .vatVND(metaData.getFee().getVatAmountVND())
                .build();
        requestContentAcc247Payment.setTranferInfo(transInfo);
        Bene247AccOutPaymentRequest bene247AccOutPaymentRequest = Bene247AccOutPaymentRequest.builder()
                .requestContent(requestContentAcc247Payment)
                .build();
        bene247AccOutPaymentRequest.getRequestHeader().setRequestID(genTranToken());
        try {
            String endpoint = env.getProperty("bilateral.endpoint.payment");
            String user = env.getProperty("bilateral.basicAuthen.user");
            String pass = env.getProperty("bilateral.basicAuthen.pass");
            String responseBank = restClient.post(endpoint, gson.toJson(bene247AccOutPaymentRequest), user, pass);
            Bene247AccOutPaymentResponse transferInBankResponse = gson.fromJson(responseBank, Bene247AccOutPaymentResponse.class);
            if (transferInBankResponse.getResponseStatus().getResponseCode().equals("105")) {
                log.info("Hạch toán timeout");
                cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                cachedSmeTrans.setTranxNote("Giao dịch timed out");
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));

                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
            } else if (transferInBankResponse.getResponseStatus().getResponseCode().equals("108")) {
                log.info("Hạch toán thành công, đẩy lệnh báo có timeout");
                cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                cachedSmeTrans.setTranxNote("Giao dịch timed out");
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));

                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
            } else if (!transferInBankResponse.getResponseStatus().getResponseCode().equals("100")) {
                cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setTranxRefno(transferInBankResponse.getResponseStatus().getTraceNo());
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
                BaseTransactionResponse data = new BaseTransactionResponse();
                data.setTranDate(CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                data.setTranxId(String.valueOf(transferInBankResponse.getResponseStatus().getTraceNo()));
                baseResp.setData(data);
            }
            if (transferInBankResponse.getPostedResponse() != null) {
                metaData.setHostDate(transferInBankResponse.getPostedResponse().getHostDatePosting());
                metaData.setPcTime(transferInBankResponse.getPostedResponse().getPcTimePosting());
                metaData.setSequence(Integer.parseInt(transferInBankResponse.getPostedResponse().getSequencePosting()));
                metaData.setTellerId(transferInBankResponse.getPostedResponse().getTellerPosting());
                cachedSmeTrans.setTeller(transferInBankResponse.getPostedResponse().getTellerPosting());
                cachedSmeTrans.setMetadata(gson.toJson(metaData));
            }
            smeTransRepository.save(cachedSmeTrans);
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cache.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }


    public BaseClientResponse execTransfer247ViaCard(BaseClientResponse baseResp,
                                                     SmeTrans cachedSmeTrans,
                                                     String hostDate,
                                                     BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        metaData.setTellerId(teller);
        metaData.setSequence(seq);
        metaData.setHostDate(hostDate);
        metaData.setPcTime(pcTime);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        //build request
        RequestContentAcc247Payment requestContentAcc247Payment = RequestContentAcc247Payment.builder()
                .creditInfo(metaData.getCreditAccount())
                .debitInfo(metaData.getDebitAccount())
                .build();
        RequestContentAcc247TransInfo transInfo = RequestContentAcc247TransInfo.builder()
                .adviceRoute(metaData.getAdviceRoute())
                .approvalTime(cachedSmeTrans.getApprovedDate() != null ? DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getApprovedDate()) : DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getTranxTime()))
                .createdTime(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss").format(cachedSmeTrans.getTranxTime()))
                .creditAmount(metaData.getCreditAccount().getOriginAmount())
                .creditCurrency(metaData.getCreditAccount().getCurrency())
                .creditLCEAmount(metaData.getCreditAccount().getAmountVND())
                .debitAmount(metaData.getDebitAccount().getOriginAmount())
                .debitCurrency(metaData.getDebitAccount().getCurrency())
                .debitLCEAmount(metaData.getDebitAccount().getAmountVND())
                .feeAmount(metaData.getFee().getOriginAmount())
                .feeCurrency(metaData.getFee().getCurrency())
                .feeVND(metaData.getFee().getAmountVND())
                .remark(
                        cachedSmeTrans
                                .getTranxRemark()
                                .replace("$1", String.valueOf(cachedSmeTrans.getId())))
                .tranferContent(cachedSmeTrans.getTranxContent())
                .tranferAmount(metaData.getCreditAccount().getAmountVND())
                .tranferBranch(metaData.getDebitAccount().getBranch())
                .tranferCurrency(metaData.getCreditAccount().getCurrency())
                .tranferRate(Double.parseDouble(metaData.getDebitAccount().getRate()))
                .vatAmount(metaData.getFee().getOriginVatAmount())
                .vatCurrency(metaData.getFee().getCurrency())
                .vatVND(metaData.getFee().getVatAmountVND())
                .build();
        requestContentAcc247Payment.setTranferInfo(transInfo);
        Bene247AccOutPaymentRequest bene247AccOutPaymentRequest = Bene247AccOutPaymentRequest.builder()
                .requestContent(requestContentAcc247Payment)
                .build();
        bene247AccOutPaymentRequest.getRequestHeader().setRequestID(genTranToken());
        try {
            String endpoint = env.getProperty("bilateral.endpoint.payment");
            String user = env.getProperty("bilateral.basicAuthen.user");
            String pass = env.getProperty("bilateral.basicAuthen.pass");
            String responseBank = restClient.post(endpoint, gson.toJson(bene247AccOutPaymentRequest), user, pass);
            Bene247AccOutPaymentResponse transferInBankResponse = gson.fromJson(responseBank, Bene247AccOutPaymentResponse.class);
            if (transferInBankResponse.getResponseStatus().getResponseCode().equals("105")) {
                log.info("Hạch toán timeout");
                cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                cachedSmeTrans.setTranxNote("Giao dịch timed out");
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));

                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
            } else if (transferInBankResponse.getResponseStatus().getResponseCode().equals("108")) {
                log.info("Hạch toán thành công, đẩy lệnh báo có timeout");
                cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                cachedSmeTrans.setTranxNote("Giao dịch timed out");
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));

                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
            } else if (!transferInBankResponse.getResponseStatus().getResponseCode().equals("100")) {
                cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
                baseResp.setCode(Constants.MessageCode.MSG_247_ERR + "-" + metaData.getAdviceRoute() + "-" + transferInBankResponse.getResponseStatus().getResponseCode());
                baseResp.setMessage(commonService.getMessage(baseResp.getCode(), rq.getLang()));
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setTranxRefno(transferInBankResponse.getResponseStatus().getTraceNo());
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResponseCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResponseMessage());
                BaseTransactionResponse data = new BaseTransactionResponse();
                data.setTranDate(CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                data.setTranxId(String.valueOf(transferInBankResponse.getResponseStatus().getTraceNo()));
                baseResp.setData(data);
            }
            if (transferInBankResponse.getPostedResponse() != null) {
                metaData.setHostDate(transferInBankResponse.getPostedResponse().getHostDatePosting());
                metaData.setPcTime(transferInBankResponse.getPostedResponse().getPcTimePosting());
                metaData.setSequence(Integer.parseInt(transferInBankResponse.getPostedResponse().getSequencePosting()));
                metaData.setTellerId(transferInBankResponse.getPostedResponse().getTellerPosting());
                cachedSmeTrans.setTeller(transferInBankResponse.getPostedResponse().getTellerPosting());
                cachedSmeTrans.setMetadata(gson.toJson(metaData));
            }
            smeTransRepository.save(cachedSmeTrans);
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cache.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    public BaseClientResponse execCashTransfer(BaseClientResponse baseResp,
                                                     SmeTrans cachedSmeTrans,
                                                     BaseConfirmRq rq) {
        String metaStr = cachedSmeTrans.getMetadata();
        TransactionMetaDataDTO metaData = gson.fromJson(metaStr, TransactionMetaDataDTO.class);

        MbService mbService =
                mbServiceRepository.findByServiceCode(cachedSmeTrans.getTranxType()).get();

        String teller = mbService.getTellerId();
        int seq = (int) (cachedSmeTrans.getId() % 100000);
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        metaData.setTellerId(teller);
        metaData.setSequence(seq);
        metaData.setPcTime(pcTime);
        cachedSmeTrans.setTeller(teller);
        cachedSmeTrans.setMetadata(gson.toJson(metaData));

        RecipientDTO recipient = metaData.getRecipient();
        String issuedPlace = CommonUtils.removeAccent(recipient.getIssuedPlace());
        recipient.setIssuedPlace(issuedPlace);
        CashTransferBankRequest transfer =
                CashTransferBankRequest.builder()
                        .amountVND(metaData.getAmountVND())
                        .originAmount(metaData.getOriginAmount())
                        .content(cachedSmeTrans.getTranxRemark())
                        .creditAccount(metaData.getCreditAccount())
                        .debitAccount(metaData.getDebitAccount())
                        .recipient(recipient)
                        .fee(metaData.getFee())
                        .originCurrency(metaData.getDebitAccount().getCurrency())
                        .pcTime(pcTime)
                        .remark(
                                cachedSmeTrans
                                        .getTranxRemark()
                                        .replace("$1", String.valueOf(cachedSmeTrans.getId()))
                                        .replace("$2", String.valueOf(seq)))
                        .sequence(seq)
                        .traceId(seq)
                        .tellerBranch(6800)
                        .tellerId(teller)
                        .build();
        try {
            CashTransferBankResponse transferInBankResponse = digiCoreTransClient.cashTransfer(transfer);
            if (!transferInBankResponse.getResponseStatus().getResCode().equals("0")) {
                // Cập nhật trạng thái giao dịch
                if (transferInBankResponse.getResponseStatus().getIsTimeout()) {
                    cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    cachedSmeTrans.setTranxNote("Giao dịch timed out");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                } else {
                    cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
                    cachedSmeTrans.setTranxNote("Giao dịch lỗi");
                    baseResp.setCode(Constants.ResCode.ERROR_96);
                    baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
                }
                metaData.setHostDate(transferInBankResponse.getHostDate());
                cachedSmeTrans.setMetadata(gson.toJson(metaData));
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                smeTransRepository.save(cachedSmeTrans);
            } else {
                // Update trạng thái giao dich
                cachedSmeTrans.setStatus(Constants.TransStatus.SUCCESS);
                metaData.setHostDate(transferInBankResponse.getHostDate());
                cachedSmeTrans.setMetadata(gson.toJson(metaData));
                cachedSmeTrans.setTranxNote("Thành công");
                cachedSmeTrans.setTranxRefno(transferInBankResponse.getMsgID());
                cachedSmeTrans.setResBankCode(transferInBankResponse.getResponseStatus().getResCode());
                cachedSmeTrans.setResBankDesc(transferInBankResponse.getResponseStatus().getResMessage());
                smeTransRepository.save(cachedSmeTrans);
                BaseTransactionResponse data = new BaseTransactionResponse();
                data.setTranDate(CommonUtils.formatLocalDateTime(LocalDateTime.now()));
                data.setTranxId(String.valueOf(transferInBankResponse.getMsgID()));
                baseResp.setData(data);
            }
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            cachedSmeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            cachedSmeTrans.setStatus(Constants.TransStatus.FAIL);
            cachedSmeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        cache.pushTxn(rq, rq.getTranToken(), cachedSmeTrans);
        smeTransRepository.save(cachedSmeTrans);
        return baseResp;
    }

    public String validatePromCode(String pkgCode, String promCode, String branchCode) {
        try {
            if (StringUtils.isNotBlank(promCode)) {
                log.info("Validate promotion code {} and package code {}", promCode, pkgCode);
                Optional<MbPkgProm> entityOpt = mbPkgPromRepository.findByPromCodeAndPkgCodeAndStatus(promCode, pkgCode, "1");
                if (entityOpt.isPresent()) {
                    // Validate date
                    MbPkgProm mbPkgProm = entityOpt.get();
                    LocalDate now = LocalDate.now();
                    if (!mbPkgProm.getValidDate().toLocalDate().isAfter(now) && !mbPkgProm.getExpiredDate().toLocalDate().isBefore(now)) {
                        log.info("Validate user branch: {}", branchCode);
                        if (StringUtils.isNotBlank(mbPkgProm.getBranches())) {
                            List<String> branches = Arrays.asList(mbPkgProm.getBranches().trim().split("\\s*,\\s*"));
                            log.info("List branch: {}", branches);
                            if (branches.contains(branchCode)) {
                                return promCode;
                            }
                        } else {
                            log.info("List branch is All");
                            return promCode;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("Failed to validate promotion code: ", e);
        }
        return null;
    }

    /**
     * Gửi báo có và ghi log Thuế nội địa
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execDomesticTax(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        TaxPaymentInfoDTO taxPaymentInfo = metadata.getTaxPaymentInfo();
        TransactionDataDTO transactionData = taxPaymentInfo.getTransactionData();
        transactionData.setSequence(seq);
        transactionData.setOrgSequence(seq);
        transactionData.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        // transactionData.setRemark(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())));
        taxPaymentInfo.setTransactionData(transactionData);
        metadata.setTaxPaymentInfo(taxPaymentInfo);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Gửi báo có và ghi log
        try {
            SendPaymentDataAndTransLogBankRequest bankReq = SendPaymentDataAndTransLogBankRequest.builder()
                    .cif(taxPaymentInfo.getCif())
                    .taxPaymentInfo(taxPaymentInfo)
                    .build();
            SendPaymentDataAndTransLogBankResponse bankResp = vcbServiceGWClient.sendPaymentDataAndTransLog(bankReq);

            smeTrans.setTranxRefno(bankResp.getSequence());
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);
                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("DOMESTIC-TAX-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setPcTime(transactionData.getPcTime());
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    /**
     * Hạch toán BHXH
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execBhxh(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        BHXHDataDTO bhxhData = metadata.getBhxhData();
        TransactionDataDTO transactionData = bhxhData.getTransaction();
        transactionData.setSequence(seq);
        transactionData.setOrgSequence(seq);
        transactionData.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        transactionData.setRemark(smeTrans.getTranxRemark());
        bhxhData.setTransaction(transactionData);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Hạch toán BHXH
        try {
            BHXHPaymentBankRequest bankReq = BHXHPaymentBankRequest.builder()
                    .cif(bhxhData.getTransaction().getDebitAccount().getCif())
                    .tax(bhxhData)
                    .build();
            BHXHPaymentBankResponse bankResp = vcbServiceGWClient.bhxhPayment(bankReq);
            smeTrans.setTranxRefno(bankResp.getSequence());
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("BHXH-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setBhxhData(bhxhData);
            metadata.setPcTime(transactionData.getPcTime());
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    /**
     * Hạch toán thu phí cảng biển
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execSeaport(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        GetSeaPortPaymentInfoDTO seaportPaymentInfo = metadata.getSeaportPaymentInfo();
        TransactionDataDTO transaction = seaportPaymentInfo.getTransaction();
        transaction.setSequence(seq);
        transaction.setOrgSequence(seq);
        transaction.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        seaportPaymentInfo.setTransaction(transaction);
        metadata.setSeaportPaymentInfo(seaportPaymentInfo);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Hạch toán BHXH
        try {
            SeaPortPaymentBankRequest bankReq = SeaPortPaymentBankRequest.builder()
                    .cif(seaportPaymentInfo.getCif())
                    .tax(seaportPaymentInfo)
                    .build();
            SeaPortPaymentBankResponse bankResp = vcbServiceGWClient.seaportPayment(bankReq);

            smeTrans.setTranxRefno(bankResp.getSequence());
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("SEAPORT-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setPcTime(transaction.getPcTime());
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    /**
     * Hạch toán thu phí cảng biển HCM
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execSeaportHCM(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        GetSeaPortPaymentInfoHCMDTO seaportPaymentInfo = metadata.getSeaportPaymentInfoHCM();
        seaportPaymentInfo.getTransaction().setSequence(seq);
        seaportPaymentInfo.getTransaction().setOrgSequence(seq);
        seaportPaymentInfo.getTransaction().setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        metadata.setSeaportPaymentInfoHCM(seaportPaymentInfo);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Hạch toán BHXH
        try {
            SeaPortPaymentHCMBankRequest bankReq = SeaPortPaymentHCMBankRequest.builder()
                    .cif(seaportPaymentInfo.getCif())
                    .tax(seaportPaymentInfo)
                    .build();
            SeaPortPaymentHCMBankResponse bankResp = vcbServiceGWClient.seaportPaymentHCM(bankReq);

            smeTrans.setTranxRefno(bankResp.getSequence());
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("SEAPORT-HCM-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setTellerId(smeTrans.getTeller());
            metadata.setSequence(seq);
            metadata.setPcTime(seaportPaymentInfo.getTransaction().getPcTime());
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    /**
     * Gửi báo có và ghi log Thuế trước bạ
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execRegistrationTax(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        RegistrationTaxDTO taxPaymentInfo = metadata.getRegistrationTax();
        TransactionDataDTO transactionData = taxPaymentInfo.getTransactionData();
        transactionData.setSequence(seq);
        transactionData.setOrgSequence(seq);
        transactionData.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        // transactionData.setRemark(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())));
        taxPaymentInfo.setTransactionData(transactionData);
        metadata.setRegistrationTax(taxPaymentInfo);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Gửi báo có và ghi log
        try {
            SendPaymentDataAndTransLogLPTBBankRequest bankReq = SendPaymentDataAndTransLogLPTBBankRequest.builder()
                    .registrationTax(taxPaymentInfo)
                    .build();
            SendPaymentDataAndTransLogLPTBBankResponse bankResp = vcbServiceGWClient.sendPaymentDataAndTransLogLPTB(bankReq);

            smeTrans.setTranxRefno(bankResp.getSequence());
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("REG-TAX-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setPcTime(transactionData.getPcTime());
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    public BaseClientResponse execImportExportTax(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        CustomsTaxInfoDTO customsTaxInfo = metadata.getCustomsTaxInfo();
        TransactionDataDTO transactionData = customsTaxInfo.getTransactionData();
        transactionData.setSequence(seq);
        transactionData.setOrgSequence(seq);
        transactionData.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        // transactionData.setRemark(smeTrans.getTranxRemark().replace("$1", String.valueOf(smeTrans.getId())));
        customsTaxInfo.setTransactionData(transactionData);
        metadata.setCustomsTaxInfo(customsTaxInfo);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        // Gửi báo có và ghi log
        try {
            String hostDate;
            ResponseStatus responseStatus;
            if ("2".equals(metadata.getChargeType())) {
                XacNhanNopPhiHqBankRequest bankReq = XacNhanNopPhiHqBankRequest.builder()
                        .cif(customsTaxInfo.getCif())
                        .customsTaxInfo(customsTaxInfo)
                        .build();
                XacNhanNopPhiHqBankResponse bankResp = vcbServiceGWClient.xacNhanNopPhiHq(bankReq);
                hostDate = bankResp.getHostDate();
                responseStatus = bankResp.getResponseStatus();
            } else {
                XacNhanNopThueHqBankRequest bankReq = XacNhanNopThueHqBankRequest.builder()
                        .cif(customsTaxInfo.getCif())
                        .customsTaxInfo(customsTaxInfo)
                        .build();
                XacNhanNopThueHqBankResponse bankResp = vcbServiceGWClient.xacNhanNopThueHQ(bankReq);
                hostDate = bankResp.getHostDate();
                responseStatus = bankResp.getResponseStatus();
            }
            smeTrans.setResBankCode(responseStatus.getResCode());
            smeTrans.setResBankDesc(responseStatus.getResMessage());

            if (!responseStatus.getIsSuccess()) {
                // Update trạng thái giao dich
                if (responseStatus.getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(responseStatus.getResCode());
                baseResp.setMessage(commonService.getMessage("IMPORT-EXPORT-TAX-CONFIRM-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setPcTime(transactionData.getPcTime());
            metadata.setHostDate(hostDate);
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(responseStatus.getResCode());
            smeTrans.setResBankDesc(responseStatus.getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);

        return baseResp;
    }

    public BaseClientResponse execCreditCardPayment(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        CardStatementUpdateBankRequest bankReq = metadata.getCreditCardPayment();
        bankReq.setSequence(seq);
        bankReq.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
        metadata.setCreditCardPayment(bankReq);
        metadata.setTellerId(smeTrans.getTeller());
        metadata.setSequence(seq);
        smeTrans.setMetadata(gson.toJson(metadata));

        try {
            CardStatementUpdateBankResponse bankResp = cardClient.creditCardPayment(bankReq);
            if (!bankResp.getResponseStatus().getIsSuccess()) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(commonService.getMessage("CARD-CREDIT-PAYMENT-" + baseResp.getCode(), req.getLang()));
                return baseResp;
            }
            metadata.setTellerId(smeTrans.getTeller());
            metadata.setSequence(seq);
            metadata.setPcTime(CommonUtils.TimeUtils.format("HHmmss", new Date()));
            metadata.setHostDate(bankResp.getHostDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        cache.pushTxn(req, req.getTranToken(), smeTrans);
        smeTransRepository.save(smeTrans);
        return baseResp;
    }

    /**
     * thanh toán tiền vay
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execLoanPayment(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        MbService mbService = mbServiceRepository.findByServiceCode(smeTrans.getTranxType()).get();
        String teller = mbService.getTellerId();
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        RepaymentLNAccountRequest bankReq = metadata.getRepaymentLNAccRequest();
        bankReq.setTrnTime(pcTime);
        smeTrans.setTeller(teller);
        metadata.setRepaymentLNAccRequest(bankReq);
        metadata.setTellerId(teller);
        metadata.setSequence(seq);
        metadata.setPcTime(pcTime);
        smeTrans.setMetadata(gson.toJson(metadata));

        try {
            GetBankHostDateResponse hostDateResponse =
                    coreQueryClient.getHostDate(new BaseBankRequest());
            bankReq.setEffDate(hostDateResponse.getCurrentDate());
            metadata.setHostDate(hostDateResponse.getCurrentDate());
            smeTrans.setMetadata(gson.toJson(metadata));
            //check số tiền trước khi duyệt
            //truy vấn thông tin phía trả nợ trước hạn
            String[] hostdateArr = hostDateResponse.getCurrentDate().split("T");
            GetFeeLNRepaymentRequest feeLNRepaymentRq = GetFeeLNRepaymentRequest.builder()
                    .lnAccount(smeTrans.getToAcc())
                    .lnAccountAlias(bankReq.getToOldAcctNo())
                    .amountRepayment(smeTrans.getTotalAmount())
                    .lnAccountCurr(bankReq.getToAcctType())
                    .effectiveDate(hostdateArr[0])
                    .build();
            GetFeeLNRepaymentResponse feeLNRepaymentResponse = coreQueryClient.getFeeLNRepayment(feeLNRepaymentRq);
            if (!"0".equals(feeLNRepaymentResponse.getResponseStatus().getResCode())) {
                log.info("Fail getFeeLNRepayment ");
                baseResp.setCode(feeLNRepaymentResponse.getResponseStatus().getResCode());
                baseResp.setMessage(feeLNRepaymentResponse.getResponseStatus().getResMessage());
                return baseResp;
            }
            Double amountOld = bankReq.getFromLCEAmount();
            Double amountNew = feeLNRepaymentResponse.getNetAmt() + feeLNRepaymentResponse.getMinCompAmt();
            if (Double.compare(amountOld, amountNew) !=0) { //kiểm tra số tiền phải trả
                log.info("amountOld {}", amountOld);
                log.info("amountNew {}", amountNew);
                //trả về giao dịch lỗi
                log.info("Giao dịch lệch phí thanh toán khoản vay");
                smeTrans.setStatus(Constants.TransStatus.FAIL);
                smeTrans.setTranxNote("Giao dịch lỗi");
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);
                baseResp.setCode(Constants.ResCode.INFO_79);
                baseResp.setMessage(commonService.getMessage(Constants.MessageCode.INFO_79, req.getLang()));
                return baseResp;
            }
            RepaymentLNAccountResponse bankResp = digiCoreTransClient.repaymentLNAccount(bankReq);
            if (!"0".equals(bankResp.getResponseStatus().getResCode())) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
                smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(bankResp.getResponseStatus().getResMessage());
                return baseResp;
            }
//            metadata.setHostDate(bankResp.get);
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        smeTransRepository.save(smeTrans);
        return baseResp;
    }

    /**
     * tạo giao dịch tra soát
     *
     * @param req
     * @param baseResp
     * @param smeTrans
     * @return
     */
    public BaseClientResponse execTransChargeback(BaseConfirmRq req, BaseClientResponse baseResp, SmeTrans smeTrans) {
        int seq = (int) (smeTrans.getId() % 100000);
        TransactionMetaDataDTO metadata = gson.fromJson(smeTrans.getMetadata(), TransactionMetaDataDTO.class);
        MbService mbService = mbServiceRepository.findByServiceCode(smeTrans.getTranxType()).get();
        String teller = mbService.getTellerId();
        CreateTransChargebackBankRequest bankReq = metadata.getCreateTransChargebackBankRequest();
        String pcTime = CommonUtils.TimeUtils.format("HHmmss", new Date());
        metadata.setTellerId(teller);
        metadata.setSequence(seq);
        metadata.setPcTime(pcTime);
        bankReq.getFeeDataObject().setTeller(teller);
        bankReq.getFeeDataObject().setSequece(seq);
        bankReq.getFeeDataObject().setPctime(pcTime);
        String cusCode = bankReq.getCusCode();
        if(StringUtils.isNotBlank(smeTrans.getApprovedUser())){
            cusCode = cusCode + "|" + smeTrans.getApprovedUser();
            bankReq.setCusCode(cusCode);
        }
        smeTrans.setTeller(teller);
        smeTrans.setMetadata(gson.toJson(metadata));

        //fake data
//        FeeTransDetailChargebackDTO feeDataObject = FeeTransDetailChargebackDTO.builder()
//                .accountno("1000008016")
//                .accountcurrency("VND")
//                .glaccount("430101008")
//                .feeamountogrin(33000.0)
//                .feeamountvnd(33000.0)
//                .feeamT_FLAT_VND(30000.0)
//                .feeamT_VAT_OGRIN(3000.0)
//                .feeamT_VAT_VND(3000.0)
//                .remark("IBBIZ1000009778.PTKS SME 3 chuyen tien")
//                .teller("5136")
//                .sequece(68607)
//                .pctime(pcTime)
//                .hostdate("2020-10-19T00:00:00")
//                .build();
//        CreateTransChargebackBankRequest bankReqFake = CreateTransChargebackBankRequest.builder()
//                .serviceCode("1")
//                .requestTSID("1")
//                .reasonID("1")
//                .departmentID("DVTKKH")
//                .teller("5139")
//                .sequence(9743)
//                .cif(20010424)
//                .hostDate("2020-10-19T00:00:00")
//                .tellerBrn(0)
//                .remark("IBBIZ1000009742.PTKS SME 3 chuyen tien")
//                .amount(1055000)
//                .currency("VND")
//                .cusAcct("1000007770")
//                .cusFullName("PTKS SME 3")
//                .cusCode("1691H95")
//                .pcTime("153839")
//                .type_request(0)
//                .is_auto_fee(1)
//                .feeDataObject(feeDataObject)
//                .build();

        try {
            GetBankHostDateResponse hostDateResponse =
                    coreQueryClient.getHostDate(new BaseBankRequest());
            bankReq.getFeeDataObject().setHostdate(hostDateResponse.getCurrentDate());
            CreateTransChargebackBankResponse bankResp = vcbServiceGWClient.createTransChargeback(bankReq);
            if (!"0".equals(bankResp.getResponseStatus().getResCode())) {
                // Update trạng thái giao dich
                if (bankResp.getResponseStatus().getIsTimeout()) {
                    smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
                    smeTrans.setTranxNote("Giao dịch timed out");
                } else {
                    smeTrans.setStatus(Constants.TransStatus.FAIL);
                    smeTrans.setTranxNote("Giao dịch lỗi");
                }
                cache.pushTxn(req, req.getTranToken(), smeTrans);
                smeTransRepository.save(smeTrans);

                baseResp.setCode(bankResp.getResponseStatus().getResCode());
                baseResp.setMessage(bankResp.getResponseStatus().getResMessage());
                return baseResp;
            }
            metadata.setHostDate(bankResp.getHostDate());
            metadata.setTsoLRef(bankResp.getTsoL_REF());
            smeTrans.setTsolRef(bankResp.getTsoL_REF());
            smeTrans.setMetadata(gson.toJson(metadata));
            smeTrans.setResBankCode(bankResp.getResponseStatus().getResCode());
            smeTrans.setResBankDesc(bankResp.getResponseStatus().getResMessage());

            // Update trạng thái giao dich
            smeTrans.setStatus(Constants.TransStatus.SUCCESS);
            smeTrans.setTranxNote("Thành công");
        } catch (RetryableException ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.TIMEOUT);
            smeTrans.setTranxNote("Giao dịch timed out");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        } catch (Exception ex) {
            log.info("Error: ", ex);
            smeTrans.setStatus(Constants.TransStatus.FAIL);
            smeTrans.setTranxNote("Giao dịch lỗi");
            baseResp.setCode(Constants.ResCode.ERROR_96);
            baseResp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        smeTransRepository.save(smeTrans);
        return baseResp;
    }
}
