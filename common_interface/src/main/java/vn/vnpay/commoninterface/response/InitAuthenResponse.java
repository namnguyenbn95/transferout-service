package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InitAuthenResponse {
    private String code;
    private String dataAuthen;
    private String message;
}
