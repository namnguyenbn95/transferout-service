package vn.vnpay.dbinterface.entity.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SvcIdent {
    String svcCategory;
    String svcType;
    String billerId;
    String billerDivisionId;
    String billerDivisionName;
    String billerDivisionVnName;
    String aggregatorId;
}
