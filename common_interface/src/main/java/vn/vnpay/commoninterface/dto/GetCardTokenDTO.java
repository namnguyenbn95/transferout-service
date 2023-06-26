package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCardTokenDTO {
    private long rrn;
    private String vcbToken;
    private String token;
    private String firstcard;
    private String lastcard;
}
