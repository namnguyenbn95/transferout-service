package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.common.CommonUtils;

import java.util.Date;

@Getter
@Setter
public class BaseConfirmResponse {
    private String refNo;
    private String tranxId;
    private String tranDate = CommonUtils.format("HH:mm dd/MM/yyyy", new Date());
    private String email;
}
