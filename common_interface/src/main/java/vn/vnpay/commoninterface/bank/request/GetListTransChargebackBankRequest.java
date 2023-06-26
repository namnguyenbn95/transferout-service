package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetListTransChargebackBankRequest extends BaseBankRequest{
    private String froM_DATE;
    private String tO_DATE;
    private int cuS_CIF;
    private String tsoL_STATUS;
}
