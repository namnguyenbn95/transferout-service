package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TxnDetailDTO;

@Getter
@Setter
public class TxnDetailBankResponse extends BaseBankResponse {
    TxnDetailDTO detailTransTxn;
}
