package vn.vnpay.dbinterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {

    // Admin
    private String accountNo;
    private String curCode;         // Currency của tk (VND...)
    private String isView;
    private String isTrans;

    // Account list from bank
    private double avaiableAmount;  // Số dư khả dụng

    // Số dư gốc
    private double accountBal;

    // Loại tài khoản của khách hàng (D: Thanh toán; S: Tiết kiệm không kỳ hạn; T: Tiết kiệm có kỳ hạn; L: Vay)
    private String accountType;

    // Ngày đáo hạn
    private String matDate;

    // Số tài khoản alias của khách hàng
    private String accountAlias;

    // Có là tài khoản joinAccount hay không
    private boolean isJoinAct;

    // Mã sản phẩm của tài khoản
    private String productCode;

    // trạng thái của tài khoản
    private String accountStatus;
}
