package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardListByCifBankRequest extends BaseBankRequest {
    private String cif;
}
