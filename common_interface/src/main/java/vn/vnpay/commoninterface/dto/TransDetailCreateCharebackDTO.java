package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransDetailCreateCharebackDTO {
    private String tsolRef;
    private int requestTSID;                     //mã yêu cầu
    private String requestTSName;                   //tên yêu cầu
    private int reasonID;                  //mã lí do
    private String reasonName;                //tên lí do
    private String debitAcc;
    private double feeFLAT;                 //phí không gồm vat
    private double feeVAT;                  //tiền vat
    private String ccy;
    private String createdUser;
    private String createdDate;
    private String approvedUser;
    private String approvedDate;
    private String tsolComment;
    private DataChangeTransChargebackRespDTO tsolDetail;
}
