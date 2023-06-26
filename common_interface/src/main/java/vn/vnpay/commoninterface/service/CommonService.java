package vn.vnpay.commoninterface.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.vnpay.commoninterface.bank.request.AccountListingBankRequest;
import vn.vnpay.commoninterface.bank.request.CardListByCifBankRequest;
import vn.vnpay.commoninterface.bank.request.GetCustomerInforByCifBankRequest;
import vn.vnpay.commoninterface.bank.response.AccountListingBankResponse;
import vn.vnpay.commoninterface.bank.response.CardListBankResponse;
import vn.vnpay.commoninterface.bank.response.GetCustomerInforByCifBankResponse;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.CardNameDTO;
import vn.vnpay.commoninterface.dto.ListCardNameDTO;
import vn.vnpay.commoninterface.dto.FeeTransferDTO;
import vn.vnpay.commoninterface.dto.OtpDto;
import vn.vnpay.commoninterface.dto.ServiceDTO;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.feignclient.DigiCardClient;
import vn.vnpay.commoninterface.feignclient.SmeApiServiceClient;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.SendSmsRequest;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.dto.CardDTO;
import vn.vnpay.dbinterface.entity.*;
import vn.vnpay.dbinterface.repository.*;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonService {

    @Value("${isTestEnv}")
    private boolean isTestEnv;

    @Autowired
    private Gson gson;

    @Autowired
    private MbMessageRepository mbMessageRepository;

    @Autowired
    private MbConfigRepository mbConfigRepository;

    @Autowired
    private SmeCheckRepository smeCheckRepository;

    @Autowired
    @Qualifier("dbOnEntityManager")
    EntityManager entityManager;

    @Autowired
    private SmeOtpRepository smeOtpRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private SmeApiServiceClient apiServiceClient;

    @Autowired
    private SmeCustomerUserRepository smeCustomerUserRepository;

    @Autowired
    private MbServiceTypeRepository mbServiceTypeRepository;

    @Autowired
    private SmeRuleRepository smeRuleRepository;

    @Autowired
    private MBHolidayRepository mbHolidayRepository;

    @Autowired
    private SmeFuncLikeRepository smeFuncLikeRepository;

    @Autowired
    private SmeFuncRecentRepository smeFuncRecentRepository;

    @Autowired
    private MongoPhoneBookRepository mongoPhoneBookRepository;

    @Autowired
    private CoreQueryClient coreQueryClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DigiCardClient cardClient;

    @Autowired
    private MbAccountFreeTransRepository accountFreeTransRepo;

    @Autowired
    private MbAccountFreeTransServRepository accountFreeTransServRepository;

    @Autowired
    private SmeCheckerServiceRoleRepository smeCheckerServiceRoleRepository;

    @Autowired
    private SmeTransRepository smeTransRepository;

    @Autowired
    private BatchTransferRepository batchTransferRepository;

    /**
     * Lấy nội dung thông báo dựa trên code & lang
     *
     * @param code // Mã câu thông báo
     * @param lang // Ngôn ngữ câu thông báo
     * @return // Nội dung thông báo
     */
    public String getMessage(String code, String lang) {
        log.info("Message code: {}", code);
        Optional<MbMessage> msgOpt = mbMessageRepository.findByCodeAndStatus(code, "1");
        if (msgOpt.isPresent()) {
            MbMessage msg = msgOpt.get();
            String content;
            if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                content = msg.getTemplateEn();
            } else {
                content = msg.getTemplate();
            }

            // Replace nội dung message theo cấu hình
            Pattern p = Pattern.compile("\\{(.*?)}");
            Matcher m = p.matcher(content);
            try {
                while (m.find()) {
                    if (m.group().length() > 2) {
                        String configCode = StringUtils.substringBetween(m.group(), "{", "}");
                        content = StringUtils.replace(content, m.group(), getConfig(configCode));
                    }
                }
            } catch (Exception e) {
                log.info("Can not replace parameter in message: ", e);
            }
            return content;
        }
        if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
            return Constants.MessageDefault.MESAGE_NOT_FOUND_EN;
        } else {
            return Constants.MessageDefault.MESAGE_NOT_FOUND_VI;
        }
    }

    /**
     * Lấy giá trị cấu hình
     *
     * @param code // Mã cấu hình
     * @return // Giá trị cấu hình
     */
    public String getConfig(String code) {
        log.info("Config code: {}", code);
        Optional<MbConfig> configOpt = mbConfigRepository.findByCodeAndStatus(code, "1");
        return configOpt.map(MbConfig::getValue).orElse(null);
    }

    /**
     * Lấy giá trị cấu hình, nếu không tìm thấy thì dùng giá trị mặc định
     *
     * @param code         // Mã cấu hình
     * @param defaultValue // Giá trị cấu hình mặc định, trong trường hợp không tìm thấy mã cấu hình
     * @return // Giá trị cấu hình
     */
    public String getConfig(String code, String defaultValue) {
        log.info("Get config code: {}", code);
        Optional<MbConfig> configOpt = mbConfigRepository.findByCodeAndStatus(code, "1");
        if (configOpt.isPresent()) {
            return configOpt.get().getValue();
        }
        return defaultValue;
    }

    public MbConfig getConfigObj(String code) {
        log.info("Config code: {}", code);
        Optional<MbConfig> configOpt = mbConfigRepository.findByCodeAndStatus(code, "1");
        return configOpt.get();
    }

    public BaseClientResponse makeClientResponse(String code, String message) {
        BaseClientResponse resp = new BaseClientResponse();
        resp.setCode(code);
        resp.setMessage(message);
        return resp;
    }

    public String makeClientResponseString(String code, String message) {
        BaseClientResponse resp = new BaseClientResponse();
        resp.setCode(code);
        resp.setMessage(message);
        return gson.toJson(resp);
    }

    //    public List<CmBannerDTO> getListBanner(String lang) {
    //        List<CmBannerDTO> lstDto = null;
    //        List<CmBanner> lst = cmBannerRepository.findByStatus("1");
    //        if (!CollectionUtils.isEmpty(lst)) {
    //            lstDto = new ArrayList<>();
    //            for (CmBanner tmp : lst) {
    //                CmBannerDTO cmBannerDTO = new CmBannerDTO();
    //                cmBannerDTO.setBannerName(tmp.getBannerName());
    //                cmBannerDTO.setDisplayArea(tmp.getDisplayArea());
    //                cmBannerDTO.setScreenType(tmp.getScreenType());
    //                cmBannerDTO.setUrlLinkInfo(lang.equalsIgnoreCase("EN") ? tmp.getUrlLinkInfoEng()
    // : tmp.getUrlLinkInfoVn());
    //                lstDto.add(cmBannerDTO);
    //            }
    //        }
    //        return lstDto;
    //    }

    public int upsertSmeCheck(SmeCustomerUser user, String imei, String type, String source) {
        Optional<SmeCheck> checkOpt =
                smeCheckRepository.findByUsernameAndCheckType(user.getUsername(), type);
        SmeCheck smeCheck;
        if (checkOpt.isPresent()) {
            smeCheck = checkOpt.get();
            int count = smeCheck.getCount();
            count += 1;
            smeCheck.setCount(count);
        } else {
            smeCheck = new SmeCheck();
            smeCheck.setCheckType(type);
            smeCheck.setImei(imei);
            smeCheck.setUsername(user.getUsername());
            smeCheck.setCusUserId(user.getCusUserId());
            smeCheck.setCount(1);
            smeCheck.setSource(source);
            smeCheck.setCreatedDate(LocalDateTime.now());
        }
        smeCheckRepository.save(smeCheck);
        return smeCheck.getCount();
    }

    /**
     * lẤY dịch vụ hiển thị theo quyền user
     *
     * @param lang
     * @param userName
     * @return
     */
    public List<ServiceDTO> getListSvr(
            String lang, String userName, String roleType, String comfirmType) {
        List<ServiceDTO> list = new ArrayList<>();
        try {
            log.info(
                    "getListSvr lang=%s, userName=%s, roleType=%s, comfirmType=%s",
                    lang, userName, roleType, comfirmType);
            StoredProcedureQuery query =
                    entityManager
                            .createStoredProcedureQuery("sme_ibmb_home.get_pr_sv")
                            .registerStoredProcedureParameter("p_out", void.class, ParameterMode.REF_CURSOR)
                            .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_confirm_type", String.class, ParameterMode.IN)
                            .setParameter("p_role_type", roleType)
                            .setParameter("p_confirm_type", comfirmType)
                            .setParameter("p_user", userName);
            List<Object[]> personComments = query.getResultList();
            for (Object[] obj : personComments) {
                ServiceDTO serviceDTO = new ServiceDTO();
                serviceDTO.setServiceTypeCode(CommonUtils.toString(obj[0]));
                serviceDTO.setServiceCode(CommonUtils.toString(obj[1]));
                serviceDTO.setServiceName(CommonUtils.toString(obj[2]));
                serviceDTO.setServiceTypeName(CommonUtils.toString(obj[3]));
                serviceDTO.setServiceGroup(CommonUtils.toString(obj[4]));
                serviceDTO.setServiceGroupName(CommonUtils.toString(obj[5]));
                serviceDTO.setBillServiceCode(CommonUtils.toString(obj[6]));
                serviceDTO.setBillServiceName(CommonUtils.toString(obj[7]));
                list.add(serviceDTO);
            }
        } catch (Exception ex) {
            log.info("Get getListSvr: {}", ex);
        }
        return list;
    }

    /**
     * Lấy danh mục thích
     *
     * @param lang
     * @param userName
     * @param status   1. Yêu thích, 0 Lấy tất cả
     * @return
     */
    public List<ServiceDTO> getListLike(
            String lang, String userName, String status, String roleType, String comfirmType) {
        List<ServiceDTO> list = new ArrayList<>();
        try {
            StoredProcedureQuery query =
                    entityManager
                            .createStoredProcedureQuery("sme_ibmb_home.get_func_like")
                            .registerStoredProcedureParameter("p_out", void.class, ParameterMode.REF_CURSOR)
                            .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_type", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_confirm_type", String.class, ParameterMode.IN)
                            .setParameter("p_role_type", roleType)
                            .setParameter("p_confirm_type", comfirmType)
                            .setParameter("p_type", status)
                            .setParameter("p_user", userName);
            List<Object[]> personComments = query.getResultList();
            for (Object[] obj : personComments) {
                ServiceDTO serviceDTO = new ServiceDTO();
                String isLike = CommonUtils.toString(obj[0]);
                serviceDTO.setIsLike(isLike);
                serviceDTO.setServiceTypeCode(CommonUtils.toString(obj[1]));
                serviceDTO.setServiceCode(CommonUtils.toString(obj[2]));
                serviceDTO.setServiceName(CommonUtils.toString(obj[3]));
                serviceDTO.setServiceTypeName(CommonUtils.toString(obj[4]));
                serviceDTO.setIsSuggest(CommonUtils.toString(obj[5]));
                serviceDTO.setIsLastUsed(CommonUtils.toString(obj[6]));
                list.add(serviceDTO);
            }
        } catch (Exception ex) {
            log.info("Get getListLike: ", ex);
        }
        return list;
    }

    /**
     * Lưu thông tin yêu thích
     *
     * @param userName
     * @param serviceCode
     * @param status      : 0 ,1
     * @return 00 Thành công, 01 Quá số lần, 96 lỗi exception
     */
    public String saveFuncLike(String userName, String serviceCode, Long status) {
        try {
            StoredProcedureQuery query =
                    entityManager
                            .createStoredProcedureQuery("sme_ibmb_home.save_func_like")
                            .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                            .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_status", Long.class, ParameterMode.IN)
                            .setParameter("p_user", userName)
                            .setParameter("p_srv_code", serviceCode)
                            .setParameter("p_status", status);
            query.execute();
            return String.valueOf(query.getOutputParameterValue("p_out"));
        } catch (Exception ex) {
            log.info("Get saveFuncLike: ", ex);
        }
        return "96";
    }

    public OtpDto makeOtp(SmeCustomerUser user, BaseClientRequest rq, String type) {
        log.info("Make OTP for user: {} with mobile: {}", user.getUsername(), user.getMobileOtp());
        String otp = CommonUtils.genOtp(isTestEnv);

        // Lưu bản ghi OTP
        String otpExpire = getConfig("OTP_EXPIRE", "1");

        String mobileOtp = user.getMobileOtp();
        SmeOtp smeOtp = new SmeOtp();
        smeOtp.setSource(rq.getSource());
        smeOtp.setUsername(user.getUsername());
        smeOtp.setCif(user.getCif());
        smeOtp.setOtpValue(otp);
        smeOtp.setMobileNo(mobileOtp);
        smeOtp.setStatus("0");
        smeOtp.setExpireDate(LocalDateTime.now().plus(Long.parseLong(otpExpire), ChronoUnit.MINUTES));
        smeOtp.setOtpType(type);
        smeOtpRepository.save(smeOtp);

        // Nếu là gửi OTP ACTIVE => Cộng số lần gửi OTP/ngày
        if (Constants.SmeOtpType.ACTIVATE.equals(type)
                || Constants.SmeOtpType.RE_ACTIVATE.equals(type)) {
            addNumberOTPActive(user, rq.getIMEI(), rq.getSource());
        }

        OtpDto otpRp = new OtpDto();
        otpRp.setOtpId(String.valueOf(smeOtp.getOtpId()));
        otpRp.setOtpMessage(otp);
        otpRp.setOtpExpire(otpExpire);
        return otpRp;
    }

    /**
     * @param user
     * @param rq
     * @param content
     * @param type:   ACTIVE, TRANSACTION
     */
    public OtpDto sendOtp(
            SmeCustomerUser user, BaseClientRequest rq, OtpDto otpRp, String type, String content) {
        // Gửi OTP kích hoạt
        log.info("Send OTP for user: {} with mobile: {}", user.getUsername(), user.getMobileOtp());
        apiServiceClient.sendSms(
                SendSmsRequest.builder()
                        .phoneNumber(user.getMobileOtp())
                        .content(content)
                        .lang(rq.getLang())
                        .build());

        String resendOtpKey = "RESEND_OTP_" + user.getUsername() + "_" + otpRp.getOtpId();
        redisCacheService.set(resendOtpKey, type, 15, TimeUnit.MINUTES);

        otpRp.setOtpMessage(
                StringUtils.overlay(
                        user.getMobileOtp(),
                        StringUtils.repeat("*", user.getMobileOtp().length() - 7),
                        4,
                        user.getMobileOtp().length() - 3));
        return otpRp;
    }

    public String checkResendOtp(String username, String token) {
        // Kiểm tra token send otp
        String key = "RESEND_OTP_" + username + "_" + token;
        String cacheToken = redisCacheService.get(key);
        if (StringUtils.isNotBlank(cacheToken)) {
            return cacheToken;
        }
        log.info(
                "OTP cannot resend because token invalid. Username: "
                        + username
                        + " token: "
                        + token
                        + ", cacheToken: "
                        + cacheToken);
        return "";
    }

    public String checkOTPActive(SmeCustomerUser user, String checkType, String source, String imei) {
        // Kiểm tra thời gian gửi OTP liên tiếp
        String key = "send_otp_active_" + user.getUsername();
        String otp = redisCacheService.get(key);
        if (StringUtils.isNotBlank(otp)) {
            log.info("OTP cannot be sent consecutively: " + user.getUsername());
            return "ACTIVE-OTPTODAY-02";
        }

        // Kiểm tra số lần nhận mã xác nhận/ngày
        int numberOtpActive = upsertSmeCheck(user, imei, checkType, source);
        if (numberOtpActive >= Integer.parseInt(getConfig("NUMBER_OTP_ACTIVE"))) {
            log.info("The user received more than OTP active: " + numberOtpActive);
            // Thông báo nếu user đang bị khóa nhận lại mã xác nhận
            return "ACTIVE-OTPTODAY-01";
        }
        return "OK";
    }

    public void addNumberOTPActive(SmeCustomerUser user, String imei, String source) {
        // Kiểm tra thời gian gửi OTP liên tiếp
        String key = "send_otp_active_" + user.getUsername();
        String value = user.getUsername();
        redisCacheService.set(
                key, value, Long.parseLong(getConfig("RESEND_SMS_OTP")), TimeUnit.MINUTES);
        // Thêm số lần nhận OTP vào MB Check
        upsertSmeCheck(user, imei, "NUMBER_OTP_ACTIVE", source);
    }

    public int checkSmeOtp(
            SmeCustomerUser user,
            String otpId,
            String otpType,
            String otpValue,
            String checkType,
            String source) {
        Optional<SmeOtp> smeOtpOptional = smeOtpRepository.findByOtpIdAndUsernameAndOtpTypeAndStatus(Long.parseLong(otpId), user.getUsername(), otpType, "0");
        if (smeOtpOptional.isPresent()) {
            SmeOtp smeOtp = smeOtpOptional.get();
            if (smeOtp.getOtpValue().equals(otpValue)) {
                if (smeOtp.getExpireDate().isBefore(LocalDateTime.now())) { // OTP hết hiệu lực
                    log.info("Check SmeOtp: 1 ~ OTP has expired");
                    return 1;
                }

                log.info("Check SmeOtp: 0 ~ OTP is OK");
                // Cập nhật trạng thái otp
                smeOtp.setStatus("1");
                smeOtpRepository.save(smeOtp);

                // Xóa thông tin SME_CHECK
                smeCheckRepository.deleteByUsernameAndCheckType(user.getUsername(), checkType);

                // Xóa thông tin resend otp trong cache
                String resendOtpKey = "RESEND_OTP_" + user.getUsername() + "_" + otpId;
                redisCacheService.delete(resendOtpKey);

                return 0;
            } else {
                String bypassCheckOtp = getConfig("BY_PASS_CHECKOTP");
                log.info("BY_PASS_CHECKOTP status = {}", bypassCheckOtp);
                int count = upsertSmeCheck(user, "unknown", checkType, source);
                if ("1".equals(bypassCheckOtp)) {
                    if (count < Integer.parseInt(getConfig("AUTOLOCK_F", "7"))) {
                        log.info("Check SmeOtp: -1 ~ count < AUTOLOCK_F");
                        return -1;
                    } else {
                        log.info("Check SmeOtp: 4 ~ count >= AUTOLOCK_F");
                        // Cập nhật lại trạng thái user - Khóa vĩnh viễn, mời ra quầy!
                        user.setAutoUnlockTime(null);
                        user.setPreviousStatus(user.getCusUserStatus());
                        user.setCusUserStatus(Constants.UserStatus.AUTO_LOCKED_F);
                        user = smeCustomerUserRepository.save(user);
                        return 4;
                    }
                } else {
                    if (count < Integer.parseInt(getConfig("AUTOLOCK_D", "3"))) {
                        log.info("Check SmeOtp: -1 ~ count < AUTOLOCK_D");
                        return -1;
                    } else if (count == Integer.parseInt(getConfig("AUTOLOCK_D", "3"))) {
                        log.info("Check SmeOtp: 2 ~ count = AUTOLOCK_D");

                        // Khóa user
                        int autoLockTime = Integer.parseInt(getConfig("AUTOLOCK_TIME_D", "1"));
                        LocalDateTime autoUnlockTime = LocalDateTime.now().plusHours(autoLockTime);
                        user.setAutoUnlockTime(autoUnlockTime);
                        user.setPreviousStatus(user.getCusUserStatus());
                        user.setCusUserStatus(Constants.UserStatus.AUTO_LOCKED_D);
                        user = smeCustomerUserRepository.save(user);
                        return 2;
                    } else if (count > Integer.parseInt(getConfig("AUTOLOCK_D", "3"))
                            && count < Integer.parseInt(getConfig("AUTOLOCK_E", "5"))) {
                        log.info("Check SmeOtp: -1 ~ count > AUTOLOCK_D && count < AUTOLOCK_E");
                        return -1;
                    } else if (count == Integer.parseInt(getConfig("AUTOLOCK_E", "5"))) {
                        log.info("Check SmeOtp: 3 ~ count = AUTOLOCK_E");
                        // Cập nhật lại trạng thái user
                        int autoLockTime = Integer.parseInt(getConfig("AUTOLOCK_TIME_E", "12"));
                        LocalDateTime autoUnlockTime = LocalDateTime.now().plusHours(autoLockTime);
                        user.setAutoUnlockTime(autoUnlockTime);
                        user.setPreviousStatus(user.getCusUserStatus());
                        user.setCusUserStatus(Constants.UserStatus.AUTO_LOCKED_E);
                        user = smeCustomerUserRepository.save(user);
                        return 3;
                    } else if (count > Integer.parseInt(getConfig("AUTOLOCK_E", "5"))
                            && count < Integer.parseInt(getConfig("AUTOLOCK_F", "7"))) {
                        log.info("Check SmeOtp: -1 ~ count > AUTOLOCK_E && count < AUTOLOCK_F");
                        return -1;
                    } else {
                        log.info("Check SmeOtp: 4 ~ count >= AUTOLOCK_F");
                        // Cập nhật lại trạng thái user - Khóa vĩnh viễn, mời ra quầy!
                        user.setAutoUnlockTime(null);
                        user.setPreviousStatus(user.getCusUserStatus());
                        user.setCusUserStatus(Constants.UserStatus.AUTO_LOCKED_F);
                        user = smeCustomerUserRepository.save(user);
                        return 4;
                    }
                }
            }
        }
        log.info("Check SmeOtp: -1 = OTP not found");
        return -1; // OTP không hợp lệ
    }

    /**
     * Lấy tên loại dịch vụ theo mã loại dịch vụ (Lang)
     *
     * @param serviceTypeCode
     * @param lang
     * @return
     */
    public String getSrvTypeName(String serviceTypeCode, String lang) {
        List<MbServiceType> lst = mbServiceTypeRepository.findByStatus("1");
        if (!CollectionUtils.isEmpty(lst)) {
            for (MbServiceType tmp : lst) {

                if (serviceTypeCode.equals(tmp.getServicetypeCode())) {
                    if (lang.equalsIgnoreCase("EN")) {
                        return tmp.getServicetypeNameEn();
                    }
                    return tmp.getServicetypeName();
                }
            }
        }
        return "";
    }

    /**
     * Lọc danh sách tài khoản theo rule debit
     *
     * @param listAccount
     * @return
     */
    public List<AccountDTO> filterListDebitAccountByProductCode(
            List<String> listServiceCode, List<AccountDTO> listAccount) {
        log.info("List service code: {}", listServiceCode);
        if (listServiceCode == null || listServiceCode.isEmpty()) {
            log.info("List service code is empty");
            return listAccount;
        }
        List<SmeRule> ruleList = smeRuleRepository.findByStatusAndServiceCodeIn("1", listServiceCode);
        if (ruleList.isEmpty()) {
            log.info("SME_RULE not found by service code: {}", listServiceCode);
            return listAccount;
        }

        String debitAllowed = StringUtils.EMPTY;
        for (SmeRule smeRule : ruleList) {
            String debitAccStr = smeRule.getDebitAccount();
            String debitAccExtStr = smeRule.getDebitAccountExt();
            debitAllowed += Strings.nullToEmpty(debitAccStr) + Strings.nullToEmpty(debitAccExtStr);
        }
        log.info("Debit allowed: {}", debitAllowed);
        if (StringUtils.isBlank(debitAllowed)) {
            return listAccount;
        }

        String finalDebitAllowed = debitAllowed;
        listAccount.removeIf(acc -> !finalDebitAllowed.contains("(" + acc.getProductCode() + ")"));
        return listAccount;
    }

    /**
     * Lọc danh sách tài khoản theo rule credit
     *
     * @param listAccount
     * @return
     */
    public List<AccountDTO> filterListCreditAccountByProductCode(
            List<String> listServiceCode, List<AccountDTO> listAccount) {
        log.info("List service code: {}", listServiceCode);
        if (listServiceCode == null || listServiceCode.isEmpty()) {
            log.info("List service code is empty");
            return listAccount;
        }
        List<SmeRule> ruleList = smeRuleRepository.findByStatusAndServiceCodeIn("1", listServiceCode);
        if (ruleList.isEmpty()) {
            log.info("SME_RULE not found by service code: {}", listServiceCode);
            return listAccount;
        }

        String creditAllowed = StringUtils.EMPTY;
        for (SmeRule smeRule : ruleList) {
            String creditAccStr = smeRule.getCreditAccount();
            String creditAccExtStr = smeRule.getCreditAccountExt();
            creditAllowed += Strings.nullToEmpty(creditAccStr) + Strings.nullToEmpty(creditAccExtStr);
        }
        log.info("Debit allowed: {}", creditAllowed);
        if (StringUtils.isBlank(creditAllowed)) {
            return listAccount;
        }

        String finalCreditAllowed = creditAllowed;
        listAccount.removeIf(acc -> !finalCreditAllowed.contains("(" + acc.getProductCode() + ")"));
        return listAccount;
    }

    public boolean checkDuringTime(String serviceCode) {
        String checkValue = getConfig("CUTOFF_TIME_" + serviceCode);
        if (Strings.isNullOrEmpty(checkValue)) {
            checkValue = getConfig("CUTOFF_TIME_DEFAULT");
        }

        //check cutoff loan payment
        if(Constants.ServiceCode.LOAN_PAYMENT.equals(serviceCode)){
            checkValue = getConfig("LOAN_PAYMENT_CUTOFF_TIME");
            if(StringUtils.isBlank(checkValue))
                return false;
            String[] timeCutoff = checkValue.split("-");
            String startHour = timeCutoff[0].split(":")[0];
            String endHour = timeCutoff[1].split(":")[0];
            String now = String.valueOf(LocalDateTime.now().getHour());
            if(Integer.parseInt(now) >= Integer.parseInt(startHour) && Integer.parseInt(now) < Integer.parseInt(endHour))
                return false;
            else
                return true;
        }

        List<MBHoliday> listH = mbHolidayRepository.findByStatus("1");
        if (serviceCode.equals(Constants.ServiceCode.TRANS_OUT_VIA_ACCNO)
                || serviceCode.equals(Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED)
                || serviceCode.equals(Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE)) {
            for (MBHoliday h : listH) {
                String[] tmps = h.getDayOff().split(",");
                String patten = "Yearly".equals(h.getHolidayType()) ? "dd/MM" : "dd/MM/yyyy";
                String now = CommonUtils.TimeUtils.getNow(patten);
                for (String s : tmps) {
                    if (s.equals(now)) {
                        return true;
                    }
                }
            }
        }

        String[] falseTime = checkValue.split(";");
        for (String time : falseTime) {
            String startTime = time.split("-")[0];
            String endTime = time.split("-")[1];
            String now = String.valueOf(LocalDateTime.now().getHour());
            if (now.compareTo(startTime) > 0 && now.compareTo(endTime) <= 0) {
                log.info(
                        "checkDuringTime@InRunTime@startTime:"
                                + startTime
                                + "@now:"
                                + now
                                + "@endTime:"
                                + endTime);
                return true;
            }
        }
        return false;
    }

    //    public List<ServiceDTO> getListCustomerLike(String lang, String userName, String status,
    // String roleType, String comfirmType) {
    //        List<ServiceDTO> list = new ArrayList<>();
    //        try {
    //            StoredProcedureQuery query = entityManager
    //                    .createStoredProcedureQuery("sme_ibmb_home.get_func_like")
    //                    .registerStoredProcedureParameter("p_out", void.class,
    // ParameterMode.REF_CURSOR)
    //                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
    //                    .registerStoredProcedureParameter("p_type", String.class, ParameterMode.IN)
    //                    .registerStoredProcedureParameter("p_role_type", String.class,
    // ParameterMode.IN)
    //                    .registerStoredProcedureParameter("p_confirm_type", String.class,
    // ParameterMode.IN)
    //                    .setParameter("p_role_type", roleType)
    //                    .setParameter("p_confirm_type", comfirmType)
    //                    .setParameter("p_type", status)
    //                    .setParameter("p_user", userName);
    //            List<Object[]> personComments = query.getResultList();
    //            for (Object[] obj : personComments) {
    //                ServiceDTO serviceDTO = new ServiceDTO();
    //                String isLike = CommonUtils.toString(obj[0]);
    //                serviceDTO.setIsLike(isLike);
    //                serviceDTO.setServiceTypeCode(CommonUtils.toString(obj[1]));
    //                serviceDTO.setServiceCode(CommonUtils.toString(obj[2]));
    //                serviceDTO.setServiceName(CommonUtils.toString(obj[3]));
    //                serviceDTO.setServiceTypeName(CommonUtils.toString(obj[4]));
    //                serviceDTO.setIsSuggest(CommonUtils.toString(obj[5]));
    //                serviceDTO.setIsLastUsed(CommonUtils.toString(obj[6]));
    //                list.add(serviceDTO);
    //
    //            }
    //        } catch (Exception ex) {
    //            log.info("Get getListLike: ", ex);
    //        }
    //        return list;
    //    }


    /**
     * hàm check danh bạ được lưu hay chưa (thông tin nào ko có thì truyền null)
     *
     * @param serviceCode
     * @param beneBankCode mã bank thụ hưởng
     * @param username     tên đn
     * @param remindName   tên gợi nhớ
     * @param accountNo    số tk thụ hưởng
     * @param idNo         số cmnd
     * @return true nếu đã lưu
     */
    public boolean isSavedBene(String serviceCode, String beneBankCode, String username,
                               String remindName, String accountNo, String idNo, String invoiceNo) {
        log.info("isSavedBene param: serviceCode {} beneBankCode {} username {} remindName {} " +
                        "accountNo {} idNo {} invoiceNo {}", serviceCode, beneBankCode, username,
                remindName, accountNo, idNo, invoiceNo);
        try {
            // check duplicate remind name
            if (!mongoPhoneBookRepository.findByUsernameAndRemindName(username, remindName).isEmpty()) {
                log.info("remind name exist");
                return true;
            }

            if (!org.apache.logging.log4j.util.Strings.isBlank(accountNo)) {
                if (Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE.equals(serviceCode) ||
                        Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED.equals(serviceCode)) {
                    if (!mongoPhoneBookRepository
                            .findByUsernameAndAccountNoIgnoreCaseAndServiceCode(
                                    username,
                                    accountNo,
                                    Constants.ServiceCode.TRANS_IN_VIA_ACCNO_DIFF_CIF).isEmpty()) {
                        log.info("Account no existed");
                        return true;
                    }
                }

                if (Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(serviceCode) ||
                        Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED.equals(serviceCode)) {
                    if (!mongoPhoneBookRepository
                            .findByUsernameAndAccountNoIgnoreCaseAndServiceCode(
                                    username,
                                    accountNo,
                                    Constants.ServiceCode.TRANS_OUT_VIA_ACCNO).isEmpty()) {
                        log.info("Account no existed");
                        return true;
                    }
                }

                if ((Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(serviceCode)
                        || Constants.ServiceCode.FAST_TRANS_VIA_ACCNO.equals(serviceCode))
                        && !mongoPhoneBookRepository
                        .findByUsernameAndAccountNoIgnoreCaseAndBeneBankCodeAndServiceCode(
                                username,
                                accountNo,
                                beneBankCode,
                                serviceCode).isEmpty()) {
                    log.info("Account no existed");
                    return true;
                }

                if ((Constants.ServiceCode.CASH_TRANS.equals(serviceCode))
                        && !mongoPhoneBookRepository
                        .findByUsernameAndIdNoAndServiceCode(
                                username,
                                idNo,
                                serviceCode).isEmpty()) {
                    log.info("Account no existed");
                    return true;
                }

                if (Constants.ServiceCode.TRANS_IN_VIA_ACCNO_DIFF_CIF.equals(serviceCode)
                        && !mongoPhoneBookRepository
                        .findByUsernameAndAccountNoIgnoreCaseAndServiceCode(
                                username,
                                accountNo,
                                serviceCode).isEmpty()) {
                    log.info("Account no existed");
                    return true;
                }

                if (!Constants.ServiceCode.TRANS_OUT_VIA_ACCNO.equals(serviceCode)
                        && !Constants.ServiceCode.TRANS_IN_VIA_ACCNO_FUTURE.equals(serviceCode)
                        && !Constants.ServiceCode.TRANS_IN_VIA_ACCNO_SCHEDULED.equals(serviceCode)
                        && !Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_FUTURE.equals(serviceCode)
                        && !Constants.ServiceCode.TRANS_OUT_VIA_ACCNO_SCHEDULED.equals(serviceCode)
                        && !Constants.ServiceCode.FAST_TRANS_VIA_ACCNO.equals(serviceCode)
                        && !Constants.ServiceCode.TRANS_IN_VIA_ACCNO_DIFF_CIF.equals(serviceCode)
                        && !mongoPhoneBookRepository
                        .findByUsernameAndAccountNoIgnoreCase(username, accountNo).isEmpty()) {
                    log.info("Account no existed");
                    return true;
                }

                if ((Constants.ServiceCode.BILL_PAYMENT.equals(serviceCode)
                        || Constants.ServiceCode.TOPUP.equals(serviceCode))
                        || Constants.ServiceCode.TRANSFER_WALLET.equals(serviceCode)
                        && !mongoPhoneBookRepository.findByUsernameAndBillProviderCodeAndInvoiceNo(username, beneBankCode, accountNo).isEmpty()) {
                    return true;
                }
            }

//            if (Constants.ServiceCode.FAST_TRANS_VIA_CARDNO.equals(serviceCode)) {
//                if (!mongoPhoneBookRepository.findByUsernameAndVcbToken(username, accountNo).isEmpty()) {
//                    log.info("Card no existed");
//                    return true;
//                }
//            }

            if (!Strings.isNullOrEmpty(accountNo) &&
                    !mongoPhoneBookRepository.findByUsernameAndVcbTokenAndServiceCode(username, accountNo, serviceCode).isEmpty()) {
                log.info("Card no existed");
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return true;
        }
    }

    /**
     * Kiểm tra user có bị khóa quyền truy vấn hay không
     *
     * @param user
     * @param listUserAccRole
     * @return
     */
    public boolean isLockedViewAuthority(SmeCustomerUser user, List<SmeUserAccRole> listUserAccRole) {
        if (vn.vnpay.commoninterface.common.Constants.UserRole.ADMIN.equals(user.getRoleType())) {
            return false;
        }

        if (listUserAccRole.isEmpty()) {
            return false;
        }

        boolean isLocked = true;
        for (SmeUserAccRole entity : listUserAccRole) {
            if ("1".equals(entity.getStatus())) {
                isLocked = false;
                break;
            }
        }
        return isLocked;
    }

    // Luu giao dich gan day
    @Transactional
    public String saveFuncRecent(String serviceCode, String userName, String roleType, String confirmType, String channel) throws Exception {
        log.info("userName - roleType...." + userName + "/" + roleType);
        log.info("confirmType: " + confirmType + "/channel: " + channel + "/serviceCode: " + serviceCode);
        deleteFuncOldRecent(channel, userName);
        //LocalDateTime local = LocalDateTime.of(2021, 8, 22, 19, 30, 40);
        List<Integer> listHomeId = getDisplayByServiceCode(serviceCode, userName, roleType, confirmType, channel);
        if (!listHomeId.isEmpty()) {
            for (Integer displayHomeId : listHomeId) {
                Optional<SmeFuncRecent> exist =
                        smeFuncRecentRepository.findExactFuncToday(userName, displayHomeId, channel, LocalDate.now());
                SmeFuncRecent obj = new SmeFuncRecent();
                obj.setUserName(userName);
                obj.setDisplayHomeId(displayHomeId);
                if (exist.isPresent()) { // ton tai trong db
                    obj = exist.get();
                    obj.setCountAct(exist.get().getCountAct() + 1);
                    obj.setUpdateDate(LocalDateTime.now());
                } else {
                    obj.setUpdateDate(LocalDateTime.now()); //local
                    obj.setCountAct(1);
                    obj.setChannel(channel);
                }
                SmeFuncRecent rs = smeFuncRecentRepository.save(obj);
                if (null == rs) {
                    return "96";
                }
            }
        }
        return "00";
    }

    // xoa giao dich cu theo channel
    @Transactional
    public void deleteFuncOldRecent(String channel, String userName) throws SQLException {
        Integer count = Integer.parseInt(getConfig("FREQUENCY_LIKE", "3"));
        List<SmeFuncRecent> listAll =
                smeFuncRecentRepository.findByUserNameAndChannel(userName, channel);
        List<String> listID = smeFuncRecentRepository.getIdLatestByUser(userName, channel, count);  //count = 3
        log.info("list Deleted: " + Arrays.toString(listID.toArray()));
        if (listAll.size() > 2) {
            log.info("loop -------");
            List<SmeFuncRecent> listDeleted =
                    smeFuncRecentRepository.getOldFuncByUser(userName, channel, LocalDate.now(), listID);
            log.info("list Deleted: " + Arrays.toString(listDeleted.toArray()));
            if (listDeleted.size() > 0) {
                listDeleted.forEach(
                        item -> {
                            smeFuncRecentRepository.delete(item);
                        });
            }
        }

    }

    //Tim displayHome Theo serviceCode + roleUser
    public List<Integer> getDisplayByServiceCode(String serviceCode, String userName, String roleType, String confirmType, String channel) {
        log.info("findListSer(String userName, String roleType" + userName + "//" + roleType);
        List<Integer> listDisplayId = new ArrayList<>();
        try {
            StoredProcedureQuery query =
                    entityManager
                            .createStoredProcedureQuery("sme_ibmb_home.get_display_by_service")
                            .registerStoredProcedureParameter("p_out", void.class, ParameterMode.REF_CURSOR)
                            .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_role", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_confirm_type", String.class, ParameterMode.IN)
                            .registerStoredProcedureParameter("p_service_code", String.class, ParameterMode.IN)
                            .setParameter("p_user", userName)
                            .setParameter("p_channel", channel)
                            .setParameter("p_role", roleType)
                            .setParameter("p_confirm_type", confirmType)
                            .setParameter("p_service_code", serviceCode);
            List<Object[]> lst = query.getResultList();
            for (Object[] obj : lst) {
                listDisplayId.add(Integer.parseInt(CommonUtils.toString(obj[0])));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listDisplayId;
    }

    public boolean validateAccountNo(SmeCustomerUser user, String accountNo, BaseClientRequest baseReq) {
        List<AccountDTO> listAcc = user.getListAccount();
        if (listAcc.isEmpty()) {
            AccountListingBankRequest accListReq = new AccountListingBankRequest();
            accListReq.setAccountGroupType("ALL");
            accListReq.setCif(user.getCif());
            accListReq.setJoinable(true);
            AccountListingBankResponse accountListingResponse = coreQueryClient.getAccountListByCif(accListReq);
            if (!accountListingResponse.getResponseStatus().getIsSuccess()) {
                log.info("Failed to query account list");
                return false;
            }
            listAcc = modelMapper.map(accountListingResponse.getListAccount(), new TypeToken<List<AccountDTO>>() {
            }.getType());
            user.setListAccount(listAcc);

            redisCacheService.putSession(baseReq, user);
        }
        Optional<AccountDTO> validAccOpt = listAcc.stream()
                .filter(c -> Long.parseLong(accountNo) != 0 && (
                        (Long.parseLong(accountNo) == Long.parseLong(Strings.isNullOrEmpty(c.getAccountNo()) ? "0" : c.getAccountNo())
                                || Long.parseLong(accountNo) == Long.parseLong(Strings.isNullOrEmpty(c.getAccountAlias()) ? "0" : c.getAccountAlias()))))
                .findFirst();
        if (validAccOpt.isPresent()) {
            return true;
        }
        return false;
    }

    public AccountDTO validAccountNoRes(SmeCustomerUser user, String accountNo, BaseClientRequest baseReq) {
        List<AccountDTO> listAcc = user.getListAccount();
        if (listAcc.isEmpty()) {
            AccountListingBankRequest accListReq = new AccountListingBankRequest();
            accListReq.setAccountGroupType("ALL");
            accListReq.setCif(user.getCif());
            accListReq.setJoinable(true);
            AccountListingBankResponse accountListingResponse = coreQueryClient.getAccountListByCif(accListReq);
            if (!accountListingResponse.getResponseStatus().getIsSuccess()) {
                log.info("Failed to query account list");
                return null;
            }
            listAcc = modelMapper.map(accountListingResponse.getListAccount(), new TypeToken<List<AccountDTO>>() {
            }.getType());
            user.setListAccount(listAcc);

            redisCacheService.putSession(baseReq, user);
        }
        Optional<AccountDTO> validAccOpt = listAcc.stream().filter(c -> accountNo.equals(c.getAccountNo()) || accountNo.equals(c.getAccountAlias())).findFirst();
        if (validAccOpt.isPresent()) {
            return validAccOpt.get();
        }
        return null;
    }

    public String genRemarkConfirmTax(String remark) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String strDate = formatter.format(date);
        StringBuilder stringBuilder = new StringBuilder(remark);

        int position = remark.indexOf("NgayNT");
        if (position <= 0) {
            position = remark.indexOf("NNT");
            position += 4;
        } else {
            position += 7;
        }

        if (position > 0) {
            stringBuilder.replace(position, position + 8, strDate);
        }

        return stringBuilder.toString();
    }

    /**
     * hàm check danh bạ được lưu hay chưa (thông tin nào ko có thì truyền null)
     *
     * @param serviceCode
     * @param beneBankCode mã bank thụ hưởng
     * @param username     tên đn
     * @param remindName   tên gợi nhớ
     * @return true nếu đã lưu
     */
    public boolean isSavedBeneNsnn(String serviceCode, String beneBankCode, String username,
                                   String remindName, String invoiceNo, String loaiHinhThu) {
        log.info("isSavedBeneNsnn param: serviceCode {} beneBankCode {} username {} remindName {} " +
                        " invoiceNo {} loaiHinhThu {}", serviceCode, beneBankCode, username,
                remindName, invoiceNo, loaiHinhThu);
        try {
            // check duplicate remind name
            if (!mongoPhoneBookRepository.findByUsernameAndRemindName(username, remindName).isEmpty()) {
                log.info("remind name exist");
                return true;
            }

            if (!org.apache.logging.log4j.util.Strings.isBlank(loaiHinhThu) &&
                    !org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                log.info("Invoice no exist");
                return !mongoPhoneBookRepository.findByUsernameAndInvoiceNoAndLoaiHinhThu(username, invoiceNo, loaiHinhThu).isEmpty();
            }

            // validate nsnn
            if (Constants.ServiceCode.DOMESTIC_TAX_VCB.equals(serviceCode)
                    || Constants.ServiceCode.DOMESTIC_TAX.equals(serviceCode)) {
                if (!org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                    if (!mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.DOMESTIC_TAX_VCB, invoiceNo).isEmpty() ||
                            !mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.DOMESTIC_TAX_VCB, invoiceNo).isEmpty()) {

                        log.info("Invoice no exist");
                        return true;
                    }
                }
            }

            if (Constants.ServiceCode.REGISTRATION_TAX_VCB.equals(serviceCode)
                    || Constants.ServiceCode.REGISTRATION_TAX.equals(serviceCode)) {
                if (!org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                    if (!mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.REGISTRATION_TAX_VCB, invoiceNo).isEmpty() ||
                            !mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.REGISTRATION_TAX, invoiceNo).isEmpty()) {

                        log.info("Invoice no exist");
                        return true;
                    }
                }
            }

            if (Constants.ServiceCode.SEAPORT_PAYMENT_VCB.equals(serviceCode)
                    || Constants.ServiceCode.SEAPORT_PAYMENT.equals(serviceCode)) {
                if (!org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                    if (!mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.SEAPORT_PAYMENT_VCB, invoiceNo.replace("-", "")).isEmpty() ||
                            !mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.SEAPORT_PAYMENT, invoiceNo.replace("-", "")).isEmpty()) {

                        log.info("Invoice no exist");
                        return true;
                    }
                }
            }

            if (Constants.ServiceCode.SOCIAL_INSURANCE.equals(serviceCode)) {
                if (!org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                    if (!mongoPhoneBookRepository.findByUsernameAndInvoiceNoAndLoaiHinhThuAndServiceCode(username, invoiceNo, loaiHinhThu, serviceCode).isEmpty()) {

                        log.info("Invoice no exist");
                        return true;
                    }
                }
            }

            if (Constants.ServiceCode.SEAPORT_PAYMENT_HCM.equals(serviceCode)) {
                if (!org.apache.logging.log4j.util.Strings.isBlank(invoiceNo)) {
                    if (!mongoPhoneBookRepository.findByUsernameAndServiceCodeAndInvoiceNo(username, Constants.ServiceCode.SEAPORT_PAYMENT_HCM, invoiceNo.replace("-", "")).isEmpty()) {

                        log.info("Invoice no exist");
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return true;
        }
    }

    /**
     * compare 2 version
     * true: appVer >= confVer
     * false: appVer < confVer
     *
     * @param appVersion
     * @param configVersion
     * @return
     */
    public boolean compareVersionApp(String appVersion, String configVersion) {
        String[] currVer = appVersion.split("\\.");
        String[] confVer = configVersion.split("\\.");
        log.info("validVersion@appVersion:" + appVersion + "@configVersion:" + configVersion);
        for (int i = 0; i < currVer.length; i++) {
            int c1 = Integer.parseInt(currVer[i]);
            int c2 = Integer.parseInt(confVer[i]);
            if (c1 > c2) {
                return true;
            } else if (c1 < c2) {
                return false;
            } else {
                continue;
            }
        }
        return true;
    }

    public boolean validateAcctNbr(SmeCustomerUser user, String acctNbr, BaseClientRequest baseReq) {
        List<CardDTO> listCard = user.getListSmeCard();
        if (listCard.isEmpty()) {
            CardListByCifBankRequest cardListReq = new CardListByCifBankRequest();
            cardListReq.setCif(user.getCif());
            CardListBankResponse bankResponse = cardClient.getSmeCardListByCif(cardListReq);
            if (!bankResponse.getResponseStatus().getIsSuccess()
                    || bankResponse.getListCard() == null
                    || bankResponse.getListCard().isEmpty()) {
                log.info("Failed to fetch card list");
                return false;
            }
            // Xử lý card name
            processCardName(bankResponse.getListCard());
            listCard = bankResponse.getListCard();
            user.setListSmeCard(listCard);
            redisCacheService.putSession(baseReq, user);
        }
        Optional<CardDTO> validCardOpt = listCard.stream().filter(c -> acctNbr.equals(c.getAcctNbr())).findFirst();
        if (validCardOpt.isPresent()) {
            return true;
        }
        return false;
    }

    public void processCardName(List<CardDTO> listCard) {
        String listCardNameStr = getConfig("LIST-CARD-NAME", StringUtils.EMPTY);
        if (StringUtils.isNotBlank(listCardNameStr)) {
            ListCardNameDTO listCardNameDto = gson.fromJson(listCardNameStr, ListCardNameDTO.class);
            for (CardDTO cardDto : listCard) {
                for (CardNameDTO cardNameDto : listCardNameDto.getListCardName()) {
                    String prefixConfig = cardNameDto.getPrefix();
                    if (cardDto.getCrdNbr().startsWith(prefixConfig)) {
                        cardDto.setCardName(cardNameDto.getCardName());
                        break;
                    } else {
                        cardDto.setCardName(StringUtils.EMPTY);
                    }
                }
            }
        }
    }

    public String getSigCustType(BaseClientRequest req) {
        SmeCustomerUser user = redisCacheService.getCustomerUser(req);
        String sigCustType = user.getSigCustType();
        if (StringUtils.isBlank(sigCustType)) {
            // Lấy thông tin chi tiết của khách hàng
            GetCustomerInforByCifBankRequest getCusInfoReq = new GetCustomerInforByCifBankRequest();
            getCusInfoReq.setCif(user.getCif());
            GetCustomerInforByCifBankResponse bankResp = coreQueryClient.getCustomerInforByCif(getCusInfoReq);
            if (bankResp.getResponseStatus().getIsSuccess() && StringUtils.isNotBlank(bankResp.getSigCustType())) {
                sigCustType = bankResp.getSigCustType();
                user.setSigCustType(sigCustType);
                user.setVatExemptFlag(bankResp.getVatExemptFlag());
                redisCacheService.putSession(req, user);
            } else {
                log.info("Failed to get customer infor by cif: {}", user.getCif());
            }
        }
        log.info("SigCustType = {}", sigCustType);
        return sigCustType;
    }

    public String getVatExemptFlag(BaseClientRequest req) {
        SmeCustomerUser user = redisCacheService.getCustomerUser(req);
        String vatExemptFlag = user.getVatExemptFlag();
        if (StringUtils.isBlank(vatExemptFlag)) {
            GetCustomerInforByCifBankRequest bankReq = new GetCustomerInforByCifBankRequest();
            bankReq.setCif(user.getCif());
            GetCustomerInforByCifBankResponse bankResp = coreQueryClient.getCustomerInforByCif(bankReq);
            if (bankResp.getResponseStatus().getIsSuccess() && StringUtils.isNotBlank(bankResp.getVatExemptFlag())) {
                vatExemptFlag = bankResp.getVatExemptFlag();
                user.setVatExemptFlag(vatExemptFlag);
                user.setSigCustType(bankResp.getSigCustType());
                redisCacheService.putSession(req, user);
            } else {
                log.info("Failed to get customer info by cif: {}", user.getCif());
            }
        }
        log.info("vatExemptFlag = {}", vatExemptFlag);
        return vatExemptFlag;
    }

    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page < 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = page * pageSize;
        if (sourceList == null || sourceList.size() <= fromIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public FeeTransferDTO checkCreditAccountFee(String serviceCode, List<String> creditAccNo, String ccy) {
        boolean freeTranstoCreditAcc = false;
        List<MbAccountFreeTrans> lstAccFreeTrans = accountFreeTransRepo.findByAccountNoInAndStatus(creditAccNo, "1");
        LocalDateTime localDateTime = LocalDateTime.now();
        lstAccFreeTrans = lstAccFreeTrans.stream().filter(p ->
                (null == p.getFromDate() || localDateTime.compareTo(p.getFromDate()) >= 0) &&
                        (null == p.getToDate() || localDateTime.compareTo(p.getToDate()) <= 0)).collect(Collectors.toList());
        List<Long> listId = lstAccFreeTrans.stream().map(MbAccountFreeTrans::getId).collect(Collectors.toList());
        if(!listId.isEmpty()){
            Optional<MbAccountFreeTransServ> accFreeTransServOpt = accountFreeTransServRepository.findByAccFreeTransIdInAndServiceCode(listId, serviceCode);
            if (accFreeTransServOpt.isPresent()) {
                freeTranstoCreditAcc = true;
            }
        }

        if (freeTranstoCreditAcc) {
            return new FeeTransferDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, ccy);
        }
        return null;
    }

    /**
     * kiểm tra xem user checker có được thao tác với các giao dịch được tạo từ maker không
     *
     * @return
     */
    public boolean checkAuthenTransChecker(String userChecker, String userMaker, String serviceCode) {
        //kiểm tra xem có trong bảng k, nếu có thì cho pass vì checker all quyền
        List<SmeCheckerServiceRole> listCkSerRole =
                smeCheckerServiceRoleRepository.findByCusUsernameAndServiceCodeNotNull(userChecker);
        if (listCkSerRole.isEmpty())
            return true;
        String serviceCodeCheck;
        if (Constants.ServiceCode.FAST_TRANS_BILATERAL_ACCNO.equals(serviceCode))
            serviceCodeCheck = Constants.ServiceCode.FAST_TRANS_VIA_ACCNO;
        else
            serviceCodeCheck = serviceCode;
        //kiểm tra trong bảng có checker phù hợp không
        List<SmeCheckerServiceRole> serviceRoleList =
                smeCheckerServiceRoleRepository.findByCusUsernameAndMakerUserAndIsTransAndServiceCodeAndStatus(
                        userChecker, userMaker, "1", serviceCodeCheck, "1");
        if (serviceRoleList.size() > 0)
            return true;
        return false;
    }

    public Date getDate(String dateStr, String dateFormat) throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(dateStr);
    }

    public void updateBatchStatus(List<String> listTransId) {
        if (listTransId == null || listTransId.isEmpty()) {
            log.error("list trans id is null");
            return;
        }
        // cap nhat trang thái lô
        List<String> listBatchId = smeTransRepository.getListBatchIdByTranId(listTransId);
        log.info("list batch id: {}", gson.toJson(listBatchId));
        for(String s : listBatchId) {
            if (Strings.isNullOrEmpty(s)) {
                continue;
            }
            log.info("update status for batch id {}", s);
            int numOfTrans = smeTransRepository.getNumOfTranByBatchId(s);
            int numOfSuccessTrans = smeTransRepository.getNumOfTranByBatchIdAndListStatus(s,
                    Arrays.asList(Constants.TransStatus.SUCCESS, Constants.TransStatus.FAIL, Constants.TransStatus.TIMEOUT));
            int numOfRejectTrans = smeTransRepository.getNumOfTranByBatchIdAndListStatus(s,
                    Arrays.asList(Constants.TransStatus.REJECT_FAIL, Constants.TransStatus.REJECT_SUCCESS,
                            Constants.TransStatus.CANCEL_FAIL, Constants.TransStatus.CANCEL_SUCCESS));

            log.info("numOfTrans {}", numOfTrans);
            log.info("numOfSuccessTrans {}", numOfSuccessTrans);
            log.info("numOfRejectTrans {}", numOfRejectTrans);
            BatchTransfer batchTransfer = batchTransferRepository.findByFileId(Long.parseLong(s));

            if (numOfTrans == numOfSuccessTrans) {
                log.info("lo da xu ly");
                batchTransfer.setStatus(Constants.BatchTransferStatus.PROCESS_SUCCESS);
            }
            if (numOfSuccessTrans > 0 && numOfSuccessTrans < numOfTrans) {
                log.info("lo da xu ly mot phan");
                batchTransfer.setStatus(Constants.BatchTransferStatus.PROCESS_APART);
            }
            if (numOfSuccessTrans == 0) {
                if (numOfRejectTrans > 0 && numOfRejectTrans < numOfTrans) {
                    log.info("lo tu choi mot phan");
                    batchTransfer.setStatus(Constants.BatchTransferStatus.REJECT_APART);
                }
                if (numOfRejectTrans == numOfTrans) {
                    log.info("lo da tu choi");
                    batchTransfer.setStatus(Constants.BatchTransferStatus.REJECT);
                }
            }

            batchTransferRepository.save(batchTransfer);
        }
    }

    /**
     * kiểm tra date có phải holiday hay không
     * @param date ngày cần kiểm tra
     * @return
     */
    public boolean isHoliday(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // nếu là ngày chủ nhật
        if (date.getDayOfWeek().getValue() == 7) {
            return true;
        }

        List<MBHoliday> listH = mbHolidayRepository.findByStatus("1");
        log.info("listH {}", gson.toJson(listH));

            for (MBHoliday h : listH) {
                String[] tmps = h.getDayOff().split(",");
                String patten = "Yearly".equalsIgnoreCase(h.getHolidayType()) ? "dd/MM" : "dd/MM/yyyy";

                String[] dateArr = dateStr.split("/");
                String dateToCheck;
                if ("dd/MM".equalsIgnoreCase(patten)) {
                    dateToCheck = dateArr[0] + "/" + dateArr[1];
                } else {
                    dateToCheck = dateStr;
                }
                log.info("dateToCheck {}", dateToCheck);

                for (String s : tmps) {
                    if (s.equals(dateToCheck)) {
                        return true;
                    }
                }
            }

        return false;
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public boolean isOldAppVersion(BaseClientRequest req) {
        if (Constants.SOURCE_IB.equals(req.getSource())) {
            return false;
        } else {
            String configVersion = getConfig("OLD_VERSION_APP", "1.0.10").replace(".", "");
            String currentVersion = req.getAppVersion().replace(".", "");

            return Long.parseLong(currentVersion) <= Long.parseLong(configVersion);
        }
    }
}
