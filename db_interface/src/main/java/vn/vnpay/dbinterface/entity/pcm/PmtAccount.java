package vn.vnpay.dbinterface.entity.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PmtAccount {
    String acctNo;
    String acctType;
    String acctName;
    String curCode;
}
