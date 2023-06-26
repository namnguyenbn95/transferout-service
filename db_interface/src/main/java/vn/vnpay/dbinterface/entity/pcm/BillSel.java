package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BillSel {
    ArrayList<BillKey> bilKeys;
    String internalCusRefCode;
    String cifNo;
    ArrayList<SearchCond> searchCond;
}
