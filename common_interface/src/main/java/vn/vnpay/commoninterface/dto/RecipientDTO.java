package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecipientDTO {
    private String fullname;
    private String idType;
    private String id;
    private String issuedDate;
    private String issuedPlace;
}
