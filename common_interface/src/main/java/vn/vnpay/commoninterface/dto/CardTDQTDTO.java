package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardTDQTDTO {
    private String pan;
    private String token;
    private String productCode;

    // From client
    private Long rowNum;
    private String acctNbr;
}
