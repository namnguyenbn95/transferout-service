package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckBene247BankResponse extends BaseBankResponse {
    // account
    String benFullName;

    // card
    String name;

    // route for napas
    Integer adviceRoute;
}
