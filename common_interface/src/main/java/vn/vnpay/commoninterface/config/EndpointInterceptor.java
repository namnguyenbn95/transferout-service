package vn.vnpay.commoninterface.config;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.service.CaptchaService;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.commoninterface.service.RedisCacheService;
import vn.vnpay.commoninterface.service.SmeEndpointService;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.dbinterface.entity.SmeEndpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class EndpointInterceptor implements HandlerInterceptor {

    @Value("${spring.application.name}")
    private String springAppName;

    @Autowired
    private Gson gson;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SmeEndpointService smeEndpointService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private CaptchaService captchaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            if (request.getMethod().equals("GET")) {
                return true;
            }
            if (springAppName.equalsIgnoreCase("api-service")) {
                log.info("preHandle Interceptor: true <--- Ignore interception for requests to api-service");
                return true;
            }

            // Validate endpoint
            String requestUri = request.getRequestURI();
            log.info("preHandle Interceptor requestUri: {}", requestUri);

            if (requestUri.startsWith("/" + springAppName + "/v1/internal")) {
                log.info("preHandle Interceptor: true <--- Ignore interception for requests call to /internal path");
                return true;
            }

            if (requestUri.endsWith("/error")) { // Process exception in Filters that still jumps into Interceptor
                response.setContentType("application/json");
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.ERROR_96, "Internal Server Error"));
                log.info("preHandle Interceptor: false <--- Called /error endpoint");
                return false;
            }

            if (requestUri.startsWith("/auth-service/v1/captcha")) {
                log.info("preHandle Interceptor: true <--- Called /auth-service/captcha endpoint");
                return true;
            }

            List<SmeEndpoint> listEndpoint = smeEndpointService.getAllValidEndpoints();
            Optional<SmeEndpoint> endpointOpt = listEndpoint.stream().filter(ep -> ep.getEndpoint().equalsIgnoreCase(requestUri) && ep.getAppName().equalsIgnoreCase(springAppName)).findAny();

            String lang = StringUtils.EMPTY;
            String jsonData = (String) request.getAttribute("JSON_REQ");
            JsonObject jsonObj = gson.fromJson(jsonData, JsonObject.class);

            if (jsonObj.has("lang")) {
                lang = jsonObj.get("lang").getAsString();
            } else {
                lang = "vi";
            }
            if (endpointOpt.isPresent()) {
                // Verify captcha nếu bắt buộc
                if (endpointOpt.get().getCaptchaIb().equals("1") && jsonObj.get("source").getAsString().equalsIgnoreCase("IB")) {
                    log.info("captcha is required");

                    // Lấy thông tin captcha từ req
                    String captchaToken = "";
                    if (jsonObj.has("captchaToken")) {
                        captchaToken = jsonObj.get("captchaToken").getAsString();
                    }
                    String captchaValue = "";
                    if (jsonObj.has("captchaValue")) {
                        captchaValue = jsonObj.get("captchaValue").getAsString();
                    }

                    // Validate thông tin
                    if (Strings.isNullOrEmpty(captchaToken) || Strings.isNullOrEmpty(captchaValue)) {
                        log.info("captchaToken or captchaValue is required");

                        // Trả về thông báo captcha không hợp lệ
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.OK.value());
                        response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.ERROR_95, commonService.getMessage(Constants.MessageCode.CAPTCHA_01, lang)));
                        log.info("preHandle Interceptor: false <--- Invalid captchaToken or captchaValue");
                        return false;
                    }

                    // Verify captcha
                    String captchaAnswer = Strings.nullToEmpty(captchaService.get(captchaToken));
                    if (!captchaAnswer.equals(captchaValue)) {
                        log.info("invalid captcha value");

                        // Trả về thông báo captcha không hợp lệ
                        response.setContentType("application/json");
                        response.setStatus(HttpStatus.OK.value());
                        response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.ERROR_95, commonService.getMessage(Constants.MessageCode.CAPTCHA_01, lang)));
                        log.info("preHandle Interceptor: false <--- Invalid captchaToken or captchaValue");
                        return false;
                    } else {
                        log.info("verify captcha ok");
                        // xóa captcha trong cache sau khi xác thực captcha thành công
                        captchaService.delete(captchaToken);
                    }
                }

                if (endpointOpt.get().getIsPublic().equals("0")) {
                    // Check sessionId validity
                    if (jsonObj.has("user") && jsonObj.has("sessionId")) {
                        BaseClientRequest baseReq = gson.fromJson(jsonObj, BaseClientRequest.class);
                        SmeCustomerUser user = redisCacheService.getCustomerUser(baseReq);
                        if (user != null) {
                            if (!user.getCusUserStatus().equals(Constants.UserStatus.ACTIVE)) {
                                // Trả về thông báo trạng thái user không hợp lệ
                                response.setContentType("application/json");
                                response.setStatus(HttpStatus.OK.value());
                                response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.USER_105, commonService.getMessage(Constants.MessageCode.USER_100, lang)));
                                log.info("preHandle Interceptor: false <--- Invalid user status");
                                return false;
                            }
                            log.info("preHandle Interceptor: true");
                            return true;
                        }

                        // Kiểm tra user có đang trong phiên đăng nhập khác hay không
                        boolean isOnOtherLoginSession = redisCacheService.isUserOnLoginSession(jsonObj.get("user").getAsString());
                        if (isOnOtherLoginSession) {
                            log.info("User {} is on another login session", jsonObj.get("user").getAsString());
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.INFO_53, commonService.getMessage(Constants.MessageCode.INFO_53, lang)));
                            log.info("preHandle Interceptor: false <--- User {} is on another login session", jsonObj.get("user").getAsString());
                            return false;
                        }
                    }
                    // Trả về thông báo hết phiên
                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.OK.value());
                    response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.INFO_08, commonService.getMessage(Constants.MessageCode.INFO_08, lang)));
                    log.info("preHandle Interceptor: false <--- Invalid sessionId");
                    return false;
                }
                log.info("preHandle Interceptor: true");
                return true;
            }
            // Trả về thông báo endpoint không hợp lệ
            String message;
            if ("en".equalsIgnoreCase(lang) || "us".equalsIgnoreCase(lang)) {
                message = "Function not supported!";
            } else {
                message = "Chức năng này chưa được hỗ trợ thực hiện.";
            }
            response.setContentType("application/json");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().write(commonService.makeClientResponseString(Constants.ResCode.ERROR_98, message));
            log.info("preHandle Interceptor: false <--- Endpoint not supported");
            return false;
        } catch (IOException e) {
            log.info("preHandle Interceptor: false <--- Exception: ", e);
            return false;
        }
    }
}
