package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenMethodResponse {
    private String code;
    private String authenMethod;
}
