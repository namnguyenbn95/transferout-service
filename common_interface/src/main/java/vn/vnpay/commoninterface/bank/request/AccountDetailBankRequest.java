package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDetailBankRequest extends BaseBankRequest {
    // Số tài khoản của khách hàng
    // example: 1000000020
    private String accountNo;

    // Loại tài khoản cần truy vấn ("D","S" = Thanh toán)
    // example: D
    private String accountType;

    // Số tài khoản truyền vào là alias hay không true = có, false = không
    private boolean isAlias;
}
