package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDTO {
    private String extendData;
    private String emailReceiver;
    private String emailCode;
}
