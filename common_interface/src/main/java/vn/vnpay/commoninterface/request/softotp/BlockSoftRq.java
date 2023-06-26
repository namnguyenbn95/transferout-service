package vn.vnpay.commoninterface.request.softotp;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseSoftRequest;

@Getter
@Setter
public class BlockSoftRq extends BaseSoftRequest {
    private String userName;
    private String cif;
    private String data;
}
