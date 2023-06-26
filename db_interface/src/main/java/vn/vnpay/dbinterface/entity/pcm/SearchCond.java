package vn.vnpay.dbinterface.entity.pcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchCond {
    String fieldName;
    String searchCondition;
    String value;
}
