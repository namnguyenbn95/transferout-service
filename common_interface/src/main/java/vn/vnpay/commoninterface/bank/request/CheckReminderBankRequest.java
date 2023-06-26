package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckReminderBankRequest extends BaseBankRequest {
    private int cif;
}
