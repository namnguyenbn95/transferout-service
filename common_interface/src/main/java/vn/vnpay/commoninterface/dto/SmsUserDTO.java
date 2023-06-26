package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsUserDTO {
    private String cif;
    private String phonenumber;
    private String account;
    private String serviceLev;
    private String status;
    private String counter;
    private String code;
    private String message;
}
