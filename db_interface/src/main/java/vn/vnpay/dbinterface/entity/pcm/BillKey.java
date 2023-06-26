package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillKey {
    SvcIdent svcIdent;
    String billId;
}
