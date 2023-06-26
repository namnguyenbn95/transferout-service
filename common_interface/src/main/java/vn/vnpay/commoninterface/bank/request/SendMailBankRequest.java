package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendMailBankRequest extends RequestHeader {
    private String approver;
    private String branch;
    private String emailTo;
    private String emailCC;
    private String emailBCC;
    private String emailFrom;
    private String subject;
    private String content;
    private String addInfo;
    private String sender;

}
