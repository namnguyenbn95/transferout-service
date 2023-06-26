package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TSOLTransactionDTO;
import vn.vnpay.commoninterface.dto.TransDetailChargebackDTO;

@Getter
@Setter
public class GetDetailCreateTransChargebackBankResponse extends BaseBankResponse{
    private TSOLTransactionDTO tsoL_Transaction;
    private TransDetailChargebackDTO oriG_TRAN_DETAIL_OBJ;
    private TransDetailChargebackDTO tsoL_TRAN_DETAIL_OBJ;
    private String msgDetail;
}
