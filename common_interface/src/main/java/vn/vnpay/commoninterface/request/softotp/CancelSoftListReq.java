package vn.vnpay.commoninterface.request.softotp;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseSoftRequest;

import java.util.List;

@Getter
@Setter
public class CancelSoftListReq extends BaseSoftRequest {
    private List<String> userCancel;
}
