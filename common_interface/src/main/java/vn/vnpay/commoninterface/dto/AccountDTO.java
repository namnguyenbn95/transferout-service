package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    // CIF của khách hàng
    private String cif;

    // Số tài khoản core mới của khách hàng
    private String accountNo;

    // Số tài khoản alias của khách hàng
    private String accountAlias;

    // Tên tài khoản của khách hàng
    private String accountName;

    // Loại tài khoản của khách hàng (D: Thanh toán; S: Tiết kiệm không kỳ hạn; T: Tiết kiệm có kỳ hạn; L: Vay)
    private String accountType;

    // Currency của tk (VND...)
    private String curCode;

    // Trạng thái của tài khoản
    private String accountStatus;

    // Mã chi nhánh của tài khoản
    private String branchNo;

    // Mã sản phẩm của tài khoản
    private String productCode;

    // Số dư khả dụng
    private Double avaiableAmount;

    // Số dư gốc
    private Double accountBal;

    // Ngày giao dịch gần nhất
    private String lastActDate;

    // Ngày đáo hạn
    private String matDate;

    // Có là tài khoản joinAccount hay không
    private Boolean isJoinAct;
}
