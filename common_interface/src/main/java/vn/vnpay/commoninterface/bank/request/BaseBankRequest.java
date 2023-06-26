package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseBankRequest {
    private RequestHeader requestHeader = new RequestHeader();
}
