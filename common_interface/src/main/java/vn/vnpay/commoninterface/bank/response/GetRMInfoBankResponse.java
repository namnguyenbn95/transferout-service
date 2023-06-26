package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RMKBNNInfoDTO;

@Getter
@Setter
public class GetRMInfoBankResponse extends BaseBankResponse {
    private RMKBNNInfoDTO rmkbnnInfo;
}
