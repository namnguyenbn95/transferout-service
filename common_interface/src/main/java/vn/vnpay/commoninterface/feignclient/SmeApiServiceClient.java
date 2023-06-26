package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.request.SendEmailCommonRequest;
import vn.vnpay.commoninterface.request.SendSmsRequest;
import vn.vnpay.commoninterface.response.BaseClientResponse;

@FeignClient(name = "api-service")
public interface SmeApiServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api-service/v1/sms/send")
    BaseClientResponse sendSms(SendSmsRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/api-service/v1/common/send-email")
    BaseClientResponse sendEmailCommon(SendEmailCommonRequest req);
}
