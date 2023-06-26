package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status {
    String statusCode;
    String errorCode;
    String errorDesc;
    ErrorDetail errorDetail;
}
