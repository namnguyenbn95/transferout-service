package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInfoNPSBankResp extends BaseBankResponse {
    private String url;
}
