package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataObjOpenBankingDTO {
    private String fieldId;
    private String fieldValue;
}
