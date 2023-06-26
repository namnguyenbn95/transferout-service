package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.PmtFeeInfo;

@Getter
@Setter
public class PcmPaymentFeeResponse extends BaseBankResponse {
    PmtFeeInfo fee;
}
