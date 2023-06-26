package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.PayerRegRec;

import java.util.ArrayList;

@Getter
@Setter
public class PcmCustomerBillingInqResponse extends BaseBankResponse {
    // cus bill list
    ArrayList<PayerRegRec> payerRegRec;
}
