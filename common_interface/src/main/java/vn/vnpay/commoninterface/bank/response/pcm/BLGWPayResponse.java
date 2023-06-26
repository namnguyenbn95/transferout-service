package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;

@Getter
@Setter
public class BLGWPayResponse extends BaseBankResponse {
    long sequence;
    String hostDate;
}
