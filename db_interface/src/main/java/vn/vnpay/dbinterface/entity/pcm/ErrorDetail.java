package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetail {
    String applicationId;
    String errorCode;
    String errorDesc;
}
