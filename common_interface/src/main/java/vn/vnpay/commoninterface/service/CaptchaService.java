package vn.vnpay.commoninterface.service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service thao tác với captcha
 *
 * @author seekill
 */
@Slf4j
@Service
public class CaptchaService {

    @Autowired
    CommonService commonService;

    @Autowired
    RedisCacheService redisCacheService;

    private final String CAPTCHA_PREFIX = "captcha_";

    /**
     * Lưu thông tin captcha vào cache
     *
     * @param captchaToken
     * @param captchaValue
     * @return
     */
    public String put(String captchaToken, String captchaValue) {
        String k = CAPTCHA_PREFIX + captchaToken;
        this.redisCacheService.set(k, captchaValue, Long.parseLong(commonService.getConfig("CAPTCHA_EXPIRY")), TimeUnit.MINUTES);
        return captchaValue;
    }

    /**
     * Lấy thông tin captcha từ cache
     *
     * @param captchaToken
     * @return
     */
    public String get(String captchaToken) {
        String k = CAPTCHA_PREFIX + captchaToken;
        String v = redisCacheService.get(k);
        if (Strings.isNullOrEmpty(v)) {
            log.info("The captcha is not existing or has expired: %s", captchaToken);
            return null;
        }
        return v;
    }

    /**
     * Xóa captcha trong cache
     *
     * @param captchaToken
     */
    public void delete(String captchaToken) {
        log.info("Delete captcha from cache");
        redisCacheService.delete(CAPTCHA_PREFIX + captchaToken);
    }

    public static boolean isValidImei(String osType, String reqImei, String cusImei) {
        log.info("isValidImei: osType {} reqImei {} cusImei {}", osType, reqImei, cusImei);
        if ("ios".equalsIgnoreCase(osType)) {
            return reqImei.equalsIgnoreCase(cusImei);
        } else {
            // check imei
            log.info("CHECK IMEI!");
            String[] splImei = reqImei.split("###");
            String[] splCusImei = cusImei == null ? new String[]{""} : cusImei.split("###");

            int checkImei = 0;

            for (int i = 0; i < splImei.length; i++) {
                for (int j = 0; j < splCusImei.length; j++) {
                    if(!StringUtils.isEmpty(splCusImei[j]) && splCusImei[j].equalsIgnoreCase(splImei[i])) {
                        checkImei = 1;
                        log.info("true");
                    }
                }
            }

            return checkImei == 1;
        }
    }

    /**
     * kiem tra so thuc d co phan thap phan hay khong
     * @param d
     * @return
     */
    public boolean isNotDecimalDigits(double d) {
        return (long) d == d;
    }
}
