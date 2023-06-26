package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VipAccDTO {
    // Tài khoản số đẹp
    private String account;

    // Loại tài khoản số đẹp
    private String accType;
}
