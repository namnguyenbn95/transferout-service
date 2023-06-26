package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListCustByCIFPartnerBankRequest extends BaseBankRequest {
    private String partnerID;
    private int cif;
    private String partnerCategory;
}
