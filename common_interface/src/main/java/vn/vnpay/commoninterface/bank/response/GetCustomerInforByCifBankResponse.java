package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCustomerInforByCifBankResponse extends BaseBankResponse {

    // Địa chỉ của khách hàng line1
    private String addressLine1;

    // Địa chỉ của khách hàng line2
    private String addressLine2;

    // AddressPostalCode
    private String addressPostalCode;

    // AddressCountry
    private String addressCountry;

    // Tên khách hàng
    private String customerName;

    // Tên khách hàng
    private String customerLegalName;

    // Số id giấy tờ
    private String idNo;

    // Loại giấy tờ (chứng minh,...)
    private String idType;

    // Ngày phát hành
    private String idIssueDate;

    // Nơi cấp
    private String idIssueState;

    // Nơi cấp
    private String idIssuePlace;

    // Ngày hết hạn giấy tờ
    private String idExpiryDate;

    // Tên khách hàng
    private String customerShortName;

    // Số điện thoại khách hàng
    private String phoneNo;

    // Loại khách hàng (C = tổ chức, I = cá nhân)
    private String custClassType;

    // BankNo
    private String bankNo;

    // Mã chi nhánh
    private String branchNo;

    // Số cif
    private String cifNo;

    // IndustrialLevel
    private String industrialLevel;

    // Mã vip code
    private String vipCode;

    // Mã TaxCode
    private String taxCode;

    // Ngày cập nhật gần nhất
    private String lastUpdDate;

    // AdditionalInfo
    private String additionalInfo;

    // VATExemptFlag
    private String vatExemptFlag;

    // SIGCustType 200 = tổ chức bán buôn, 300 = sme
    private String sigCustType;

    // MsgID esb
    private String msgID;

    // MsgDetail esb
    private String msgDetail;
}
