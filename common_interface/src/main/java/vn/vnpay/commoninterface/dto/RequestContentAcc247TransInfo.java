package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestContentAcc247TransInfo {
    private Integer adviceRoute;
    //yyyyMMdd HHmmssc
    private String approvalTime;
    //yyyyMMdd HHmmssc
    private String createdTime;
    private double creditAmount;
    private String creditCurrency;
    private double creditLCEAmount;
    private double debitAmount;
    private String debitCurrency;
    private double debitLCEAmount;
    //số tiền phí
    private double feeAmount;
    //loại tiền phí
    private String feeCurrency;
    private double feeVND;
    //loai giao dich
    private String paymentType;
    // Remark giao dich
    private String remark;
    //Teller approval lệnh chuyển tiền
    private String tellerApproval;
    //Teller tạo lệnh chuyển tiền
    private String tellerCreated;
    //Số tiền chuyển đi sang ngân hàng kia
    private double tranferAmount;
    //Chi nhánh tạo lệnh
    private String tranferBranch;
    //	Loại tiền chuyển sang ngân hàng kia
    private String tranferCurrency;
    //	Tỉ giá quy đổi
    private double tranferRate;
    private double vatAmount;
    private String vatCurrency;
    private double vatVND;
    private String tranferContent;
}
