package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetFeeLNRepaymentResponse extends BaseBankResponse {
    private int penaltyPlanNo;      //Mã phí
    private double prepayAmt;       //Số tiền trả nợ gốc trước hạn
    private double repayAmt;        //Số tiền trả nợ gốc đến hạn
    private double prepayPenalty;   //Phí trả nợ trước hạn
    private double penaltyRate;     //Mức phí trả nợ trước hạn
    private String sName;           //Tên TK
    private double netAmt;          //Tổng số tiền trả nợ gốc - Chưa bao gồm số tiền bù phí
    private double minCompAmt;      //Số tiền bù phí tối thiểu
}
