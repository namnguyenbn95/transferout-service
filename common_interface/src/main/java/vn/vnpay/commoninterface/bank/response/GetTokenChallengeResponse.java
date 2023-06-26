package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTokenChallengeResponse extends BaseBankResponse {
    private String challenge;
}
