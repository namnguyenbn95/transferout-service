package vn.vnpay.dbinterface.entity.pcm;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillField {
    String id;
    String enName;
    String vnName;
    String type;
    String dataType;
    String length;
    String condition;
    String value;
    String isStandardField;
    String isFetchBillerDivision;
}
