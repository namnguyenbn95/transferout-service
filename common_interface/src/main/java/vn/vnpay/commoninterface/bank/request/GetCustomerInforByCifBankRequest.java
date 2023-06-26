package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCustomerInforByCifBankRequest extends BaseBankRequest {
    private String cif;
}
