package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VipAccTypeDTO {
    private String accType;
    private String accTypeName;
}
