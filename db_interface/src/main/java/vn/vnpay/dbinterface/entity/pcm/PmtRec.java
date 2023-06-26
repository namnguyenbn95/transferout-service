package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmtRec {
    String transRefNumber;
    String pmtStatus;
    String pmtInfo;
    String pmtAccRefNum;
    PmtAccount toAcct;
    BillRef billRef;
    String narration;
}
