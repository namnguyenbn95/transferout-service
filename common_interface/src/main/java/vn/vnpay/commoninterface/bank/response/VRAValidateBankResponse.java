package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VRAValidateBankResponse extends BaseBankResponse {
    // Tên tài khoản ảo
    private String vaName;

    // Trạng thái tài khoản ảo
    private String vaStatus;

    // Tên tài khoản thực
    private String realAccountName;

    // Số tài khoản thực
    private String realAccountNumber;
}
