package vn.vnpay.commoninterface.response.softotp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockSoftRp {
    private String code;
    private String message;
}
