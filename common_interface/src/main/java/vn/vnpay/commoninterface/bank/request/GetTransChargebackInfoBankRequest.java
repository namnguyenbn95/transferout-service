package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetTransChargebackInfoBankRequest extends BaseBankRequest {
    private String teller;
    private int sequence;
    private int cif;
    private String hostDate;
    private String remark;
    private double amount;
    private String bankAccount;
    private String pcTime;
    private int type_request;
}
