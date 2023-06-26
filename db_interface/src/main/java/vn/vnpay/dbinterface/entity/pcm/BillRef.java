package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BillRef {
    SvcIdent svcIdent;
    ArrayList<BillRec> billRec;
}
