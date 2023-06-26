package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "payment-service")
public interface SmePaymentServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/payment-service/v1/internal/reload")
    String reload();

}
