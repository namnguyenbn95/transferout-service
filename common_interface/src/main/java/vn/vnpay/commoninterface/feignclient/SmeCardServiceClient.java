package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.request.GetCardTransHisRequest;
import vn.vnpay.commoninterface.response.BaseClientResponse;

@FeignClient(name = "card-service")
public interface SmeCardServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/card-service/v1/internal/card/trans/hist-list")
    BaseClientResponse getCardTransHis(GetCardTransHisRequest req);

}
