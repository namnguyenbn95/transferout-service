package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListFutureTransDTO;

@Getter
@Setter
public class DetailFutureTransBankResponse extends BaseBankResponse {
    private ListFutureTransDTO transDetail;
}
