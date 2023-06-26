package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopReminderBankRequest extends BaseBankRequest {
    private int cif;
    private String source;
    private String teller;
    private String sequence;
    private String sup;
    private String supSequence;
    private String notiType;
    private String notiValue;
}
