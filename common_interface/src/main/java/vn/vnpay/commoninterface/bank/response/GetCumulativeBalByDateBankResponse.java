package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCumulativeBalByDateBankResponse extends BaseBankResponse {
    private String cumulativeBalance;
    private String msgDetail;
}
