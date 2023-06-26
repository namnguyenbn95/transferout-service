package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletPartnerDTO {
    String partnerId;
    String partnerName;
    String partnerCategory;
    Object partnerSettings;
    EwalletFieldDTO ewalletField;
}
