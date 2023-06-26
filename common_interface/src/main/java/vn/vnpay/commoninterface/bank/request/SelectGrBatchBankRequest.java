package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelectGrBatchBankRequest extends BaseBankRequest {
    private String refNo;
//    private String date6;//250420
//    private  String tellId;// 5087
//    private int seq;
}
