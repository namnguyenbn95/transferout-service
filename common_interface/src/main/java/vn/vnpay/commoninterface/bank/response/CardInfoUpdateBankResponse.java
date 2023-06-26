package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardInfoUpdateBankResponse extends BaseBankResponse {
    private double updatedRows;
}
