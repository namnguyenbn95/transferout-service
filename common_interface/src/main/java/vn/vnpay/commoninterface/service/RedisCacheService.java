package vn.vnpay.commoninterface.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.dbinterface.entity.SmeLimitMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private CommonService commonService;

    @Autowired
    private Gson gson;

    /**
     * set key-value redis non expire
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        try {
            key = Constants.CACHE_PREFIX + key;
            redisTemplate.opsForValue().set(key, value);
            log.info("put key = {}, ttl = {}", key);
        } catch (Exception e) {
            log.info("put cache with expire exception: " + e);
        }
    }

    /**
     * setnx key-value redis expire
     *
     * @param key
     * @param timeToLive
     */
    public boolean setnx(String key, long timeToLive) {
        try {
            key = Constants.CACHE_PREFIX + key;
            log.info("put key = {}, ttl = {}", key, timeToLive);
            return redisTemplate.opsForValue().setIfAbsent(key, "1", timeToLive, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.info("put cache with expire exception: " + e);
        }
        return false;
    }

    /**
     * set key-value redis expire
     *
     * @param key
     * @param value
     * @param timeToLive
     */
    public void set(String key, String value, long timeToLive, TimeUnit timeUnit) {
        try {
            key = Constants.CACHE_PREFIX + key;
            redisTemplate.opsForValue().set(key, value, timeToLive, timeUnit);
            log.info("put key = {}, ttl = {}", key, timeToLive);
        } catch (Exception e) {
            log.info("put cache with expire exception: " + e);
        }
    }

    /**
     * get value with key redis
     *
     * @param key
     * @return
     */
    public String get(String key) {
        try {
            key = Constants.CACHE_PREFIX + key;
            String value = (String) redisTemplate.opsForValue().get(key);
            log.info("get key = {}", key);
            if (StringUtils.isBlank(value)) {
                log.info("value is blank");
            }
            return value;
        } catch (Exception e) {
            log.info("get value with key exception: " + e);
            return null;
        }
    }

    /**
     * put data with hset
     *
     * @param hash
     * @param key
     * @param value
     */
    public void hset(String hash, String key, String value, long timeToLive, TimeUnit timeUnit) {
        try {
            hash = Constants.CACHE_PREFIX + hash;
            redisTemplate.opsForHash().put(hash, key, value);
            log.info("put hash = {}, key = {}, ttl = {}", hash, key, timeToLive);
            redisTemplate.expire(key, timeToLive, timeUnit);
        } catch (Exception e) {
            log.info("put cache hset exception: " + e);
        }
    }

    /**
     * get value with hash and key
     *
     * @param hash
     * @param key
     * @return
     */
    public String getHSet(String hash, String key) {
        try {
            hash = Constants.CACHE_PREFIX + hash;
            String value = (String) redisTemplate.opsForHash().get(hash, key);
            log.info("get hash = {}, key = {}", hash, key);
            if (StringUtils.isBlank(value)) {
                log.info("value is blank");
            }
            return value;
        } catch (Exception e) {
            log.info("get hset exception: " + e);
            return null;
        }
    }

    /**
     * get all key value with hash
     *
     * @param hash
     * @return
     */
    public Map<Object, Object> getAllHset(String hash) {
        try {
            hash = Constants.CACHE_PREFIX + hash;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(hash);
            if (map == null || map.isEmpty()) {
                log.info("get all Hset is null");
                return null;
            }
            return map;
        } catch (Exception e) {
            log.info("get all hset exception: " + e);
            return null;
        }
    }

    public void delete(String key) {
        key = Constants.CACHE_PREFIX + key;
        log.info("delete cache with key = {}", key);
        redisTemplate.delete(key);
    }

    public void deleteByPattern(String pattern) {
        pattern = Constants.CACHE_PREFIX + pattern + "*";
        Set<Object> keys = redisTemplate.keys(pattern);
        log.info("delete cache with pattern = {}", pattern);
        for (Object key : keys) {
            redisTemplate.delete(key);
        }
    }

    public void deleteHset(String hash, String key) {
        log.info("delete cache with hash = {}, key = {}", hash, key);
        redisTemplate.opsForHash().delete(hash, key);
    }

    /**
     * Lấy thông tin khách hàng từ cache
     *
     * @param baseReq
     * @return
     */
    public SmeCustomerUser getCustomerUser(BaseClientRequest baseReq) {
        try {
            // key có format: vcbsme_login_sessions_[USERNAME][SOURCE][SESSIONID]
            String k = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + baseReq.getUser() + baseReq.getSource() + baseReq.getSessionId();
            if (Constants.SOURCE_BANK_HUB.equalsIgnoreCase(baseReq.getSource())) {
                log.info("get user from source bank hub");
                k = Constants.CACHE_PREFIX_BANK_HUB + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + baseReq.getUser() + baseReq.getSource() + baseReq.getSessionId();
            }
            String v = (String) redisTemplate.opsForValue().get(k);
            if (Strings.isNullOrEmpty(v)) {
                log.info("The session is not existing or has expired key = {}", k);
                return null;
            }
            // Lấy dữ liệu thành công => Tự động expire thời gian
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Get Session: {}", k);
            return gson.fromJson(v, SmeCustomerUser.class);
        } catch (Exception ex) {
            log.error("An error occurred: " + ex);
            return null;
        }
    }

    /**
     * Lay danh sach khach hang tu cache
     *
     * @return
     */
    public List<SmeCustomerUser> getCustomers(String mobileNo) {
        try {
            ArrayList<SmeCustomerUser> smeCustomerUsers = new ArrayList<>();
            // key có format: vcbsme_login_sessions_
            String pattern = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION;
            if (!Strings.isNullOrEmpty(mobileNo)) {
                pattern += mobileNo;
            }
            pattern += "*";
            Set<Object> keys = redisTemplate.keys(pattern);
            for (Object key : keys) {
                String v = (String) redisTemplate.opsForValue().get(key.toString());
                SmeCustomerUser smeCustomerUser = gson.fromJson(v, SmeCustomerUser.class);
                smeCustomerUsers.add(smeCustomerUser);
            }

            return smeCustomerUsers;
        } catch (Exception ex) {
            log.error("An error occurred: " + ex);
            return null;
        }
    }

    public void updateSmeCustomerUserStatus(String username, String status) {
        // key có format: vcbsme_login_sessions_username
        String pattern = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            String k = keys.iterator().next().toString();
            String v = (String) redisTemplate.opsForValue().get(k);
            log.info("Get Session: " + k + " data: " + v);
            SmeCustomerUser smeCustomerUser = gson.fromJson(v, SmeCustomerUser.class);
            smeCustomerUser.setCusUserStatus(status);
            String vUpdate = gson.toJson(smeCustomerUser);
            log.info("Push Session: " + k + " data: " + vUpdate);
            redisTemplate.opsForValue().set(k, vUpdate);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session success: " + k + " data: " + vUpdate);
        }

        // update for bank hub
        String patternBankhub = Constants.CACHE_PREFIX_BANK_HUB + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> keysBankhub = redisTemplate.keys(patternBankhub);
        if (!keysBankhub.isEmpty()) {
            String k = keysBankhub.iterator().next().toString();
            String v = (String) redisTemplate.opsForValue().get(k);
            log.info("Get Session bank hub: " + k + " data: " + v);
            SmeCustomerUser smeCustomerUser = gson.fromJson(v, SmeCustomerUser.class);
            smeCustomerUser.setCusUserStatus(status);
            String vUpdate = gson.toJson(smeCustomerUser);
            log.info("Push Session bank hub: " + k + " data: " + vUpdate);
            redisTemplate.opsForValue().set(k, vUpdate);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session bank hub success: " + k + " data: " + vUpdate);
        }
    }

    /**
     * Lưu cache user
     *
     * @param baseReq
     * @param user
     */
    public void putSession(BaseClientRequest baseReq, SmeCustomerUser user) {
        try {
            // key có format: vcbsme_login_sessions_[USERNAME][SOURCE][SESSIONID]
            String k = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + user.getUsername() + baseReq.getSource() + baseReq.getSessionId();
            String v = gson.toJson(user);
            log.info("Push Session: key = {}; value = {}", k, v);
            redisTemplate.opsForValue().set(k, v);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session success: " + k);
        } catch (Exception ex) {
            log.error("An error occurred: " + ex);
        }
    }

    public void putSessionBankHub(BaseClientRequest baseReq, SmeCustomerUser user) {
        try {
            // key có format: vcbsme_login_sessions_[USERNAME][SOURCE][SESSIONID]
            String k = Constants.CACHE_PREFIX_BANK_HUB + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + user.getUsername() + baseReq.getSource() + baseReq.getSessionId();
            String v = gson.toJson(user);
            log.info("Push Session: key = {}; value = {}", k, v);
            redisTemplate.opsForValue().set(k, v);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session success: " + k);
        } catch (Exception ex) {
            log.error("An error occurred: " + ex);
        }
    }

    public void kickoutSession(String username) {
        String pattern = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        log.info("delete cache with pattern = {}", pattern);
        Set<Object> keys = redisTemplate.keys(pattern);
        for (Object key : keys) {
            redisTemplate.delete(key);
        }
    }

    public void kickoutSessionBankHub(String username) {
        String pattern = Constants.CACHE_PREFIX_BANK_HUB + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        log.info("delete cache with pattern = {}", pattern);
        Set<Object> keys = redisTemplate.keys(pattern);
        for (Object key : keys) {
            redisTemplate.delete(key);
        }
    }

    public Long increment(String key, long expire, TimeUnit unit) {
        try {
            Long result = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, expire, unit);
            log.info("Increment cache: " + key + ", value: " + result + expire + ", time_unit: " + unit.toString());
            return result;
        } catch (Exception ex) {
            log.error("Set exception: ", ex);
        }
        return null;
    }

    /**
     * Push cache kiểm tra bảo mật hạn mức lập lệnh
     *
     * @param lst
     * @param session
     * @param user
     */
    public void pushLstLmChecker(List<SmeLimitMaker> lst, String session, String user) {
        try {
            if (!CollectionUtils.isEmpty(lst)) {
                for (SmeLimitMaker checker : lst) {
                    redisTemplate.opsForHash().put(Constants.RedisKey.KEY_LIMIT_CHECKER + user + session, checker.getUserName() + checker.getServiceType() + checker.getCcy(), gson.toJson(checker));
                }
                redisTemplate.expire(Constants.RedisKey.KEY_LIMIT_CHECKER + user + session, Long.valueOf(commonService.getConfig("SESSION_EXPIRE", "10")), TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("pushLstLmChecker EX: ", e);
        }
    }

    /***
     *
     * @param session
     * @param user : User admin
     * @param userName : User được cài đặt hạn mức
     * @return
     */
    public SmeLimitMaker getLimitChecker(String session, String user, String userName) {
        String result = (String) redisTemplate.opsForHash().get(Constants.RedisKey.KEY_LIMIT_CHECKER + user + session, userName);
        log.info("getLimitChecker:" + result);
        SmeLimitMaker smeLimitChecker = gson.fromJson(result, SmeLimitMaker.class);
        if (smeLimitChecker != null) {
            redisTemplate.expire(Constants.RedisKey.KEY_LIMIT_CHECKER + user + session, Long.valueOf(commonService.getConfig("SESSION_EXPIRE", "10")), TimeUnit.MINUTES);
        }
        return smeLimitChecker;
    }


    /**
     * Đây lưu trữ giao dich
     *
     * @param rq
     * @param tranToken
     * @param dataCache
     */
    public void pushTxn(BaseClientRequest rq, String tranToken, Object dataCache) {
        try {
            String key = Constants.CACHE_PREFIX + Constants.RedisKey.KEY_TXN + rq.getUser() + tranToken;

            log.info("keyTrans: " + key);
            log.info("keyTransValue: " + gson.toJson(dataCache));
            redisTemplate.opsForValue().set(key, gson.toJson(dataCache), Long.parseLong(commonService.getConfig("SESSION_EXPIRE", "10")), TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("pushTxn EX: ", e);
        }
    }

    /**
     * Lấy giao dịch đã lưu trữ
     *
     * @param rq
     * @return
     */
    public String getTxn(BaseConfirmRq rq) {
        String key = Constants.CACHE_PREFIX + Constants.RedisKey.KEY_TXN + rq.getUser() + rq.getTranToken();
        log.info("getkeyTrans: " + key);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public Long decrement(String key) {
        try {
            Long result = this.redisTemplate.opsForValue().decrement(key);
            log.info("Decrement cache: " + key + ", value: " + result);
            return result;
        } catch (Exception ex) {
            log.error("Set exception: ", ex);
        }
        return null;
    }

    public SmeCustomerUser getSmeCustomerFromCache(String username) {
        // key có format: vcbsme_login_sessions_username
        String pattern = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            String k = keys.iterator().next().toString();
            String v = (String) redisTemplate.opsForValue().get(k);
            log.info("Get Session: " + k + " data: " + v);
            return gson.fromJson(v, SmeCustomerUser.class);
        } else {
            return null;
        }
    }

    public void updateSmeCustomerUserCache(String username, SmeCustomerUser user) {
        // key có format: vcbsme_login_sessions_username
        String pattern = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            String k = keys.iterator().next().toString();
            String v = (String) redisTemplate.opsForValue().get(k);
            log.info("Get Session: " + k + " data: " + v);
            String vUpdate = gson.toJson(user);
            log.info("Push Session: " + k + " data: " + vUpdate);
            redisTemplate.opsForValue().set(k, vUpdate);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session success: " + k + " data: " + vUpdate);
        }

        // update for bank hub
        String patternBankhub = Constants.CACHE_PREFIX_BANK_HUB + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> keysBankHub = redisTemplate.keys(patternBankhub);
        if (!keysBankHub.isEmpty()) {
            String k = keysBankHub.iterator().next().toString();
            String v = (String) redisTemplate.opsForValue().get(k);
            log.info("Get Session bank hub: " + k + " data: " + v);
            String vUpdate = gson.toJson(user);
            log.info("Push Session bank hub: " + k + " data: " + vUpdate);
            redisTemplate.opsForValue().set(k, vUpdate);
            String loginSession = commonService.getConfig("SESSION_EXPIRE", "10");
            redisTemplate.expire(k, Long.parseLong(loginSession), TimeUnit.MINUTES);
            log.info("Push Session bank hub success: " + k + " data: " + vUpdate);
        }
    }

    public boolean isUserOnLoginSession(String username) {
        String k = Constants.CACHE_PREFIX + vn.vnpay.commoninterface.common.Constants.RedisKey.KEY_LOGIN_SESSION + username + "*";
        Set<Object> loginKeys = redisTemplate.keys(k);
        if (loginKeys.isEmpty()) {
            return false;
        }
        return true;
    }
}
