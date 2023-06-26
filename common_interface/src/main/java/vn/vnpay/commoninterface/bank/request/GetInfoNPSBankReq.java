package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetInfoNPSBankReq extends BaseBankRequest {
    private String cif;
    private String branchCode;
    private String username;
    private String companyName;
    private String email;
    private boolean language;
}
