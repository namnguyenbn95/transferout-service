package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.request.AccountListTransManageRequest;

@FeignClient(name = "auth-service")
public interface AuthServiceSmeClient {
    @RequestMapping(method = RequestMethod.POST, value = "/auth-service/v1/trans/account/list")
    String getListAccTrans(AccountListTransManageRequest req);
}
