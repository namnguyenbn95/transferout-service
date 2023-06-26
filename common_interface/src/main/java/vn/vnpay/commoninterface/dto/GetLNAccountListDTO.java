package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLNAccountListDTO {
    private Long cif;
    private String customerType;
    private String accountNumber;
    private String accountName;
    private String accountAddress;
    private String accountType;
    private String currency;
    private String status;
    private int branch;
    private int groupCode;
    private String productTypeCode;
    private double availableBalance;            // dư nợ hiện tại
    private String alias;                       // tk alias
    private String facilityNo;                  // số hợp đồng vay
    private double approvedAmmout;              // số tiền phê duyệt
    private String approvedDate;                // ngày phê duyệt
    private String acctRelation;                // quan hệ khách hàng với tk
    private String openDate;                    // ngày mở tk
    private String boughtSoldCode;              // cờ tài khoản đồng tài trợ
    private String chargeOffCode;               // cờ tài khoản bị nợ xủ lí
    private String intType;                     // cờ tài khoản cho trả góp
    private String noteType;                    // loại tk vay
    private double prinDueAmount;               // dư nợ đến hạn
    private String curCode;
}
