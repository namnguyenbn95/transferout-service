package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class TxnDetailBankRequest extends BaseBankRequest {
    String transDate;
    String newAccount;
    String tellerID;
    String sequence;
    String pcTime;
    String typeTrans;
}
