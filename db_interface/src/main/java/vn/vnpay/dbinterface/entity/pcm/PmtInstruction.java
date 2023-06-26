package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmtInstruction {
    PmtAccount fromAcct;
    String pmtAccRefNum;
    String billerCreditAccRefNum;
    PmtAccount toAcct;
    PmtAmount fromAmt;
    PmtAmount toAmt;
    FeeChargeAlloc feeChargeAlloc;
    String pmtMethod;
    String payerInstructions;
    String remark;
    String internalRefNo;
    String trnDt;
    String depositSlipNumber;
    String refundAccRefNum;
    String narration;
}
