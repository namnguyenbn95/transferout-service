package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryCustomerInfoBankResponse extends BaseBankResponse {
    String customerName;
    String vcbServiceCode;
    String customerAddress;
}
