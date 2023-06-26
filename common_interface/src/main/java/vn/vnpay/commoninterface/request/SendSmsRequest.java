package vn.vnpay.commoninterface.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendSmsRequest {
    private String phoneNumber;
    private String content;
    private String lang;
}
