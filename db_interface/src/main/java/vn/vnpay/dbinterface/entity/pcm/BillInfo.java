package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BillInfo {
    BillAmt billAmt;
    String billInvoiceDt;
    String billDueDt;
    ArrayList<BillField> billField;
    BillStatus billStatus;
}
