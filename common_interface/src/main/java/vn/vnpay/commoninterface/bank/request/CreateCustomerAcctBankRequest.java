package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CreditAccountDTO;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;
import vn.vnpay.commoninterface.dto.FeeDTO;
import vn.vnpay.dbinterface.dto.DebitCardDTO;

@Getter
@Setter
@Builder
public class CreateCustomerAcctBankRequest extends BaseBankRequest {
    private String cifNo;
    //Mã sản phẩm
    private String productType;
    //InterestPlan
    private String interestPlan;
    private String branch;
    private String acctNo;
    private String teller;
    private int sequenceNumber;
}
