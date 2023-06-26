package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientIDDTO {
    private String fullname;
    private String idType;
    private String id;
    private String issuedDate;
    private String issuedPlace;
}
