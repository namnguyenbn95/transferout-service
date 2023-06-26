package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetIBPSTransStatusBankRequest extends BaseBankRequest {
    private String hostDate;
    private String teller;
    private int sequence;
    private String pctime;
    private String branch;
}
