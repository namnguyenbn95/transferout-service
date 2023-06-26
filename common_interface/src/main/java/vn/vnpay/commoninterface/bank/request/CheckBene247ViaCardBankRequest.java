package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckBene247ViaCardBankRequest extends BaseBankRequest {
    String cardToken;
    String acqBin;
    @Builder.Default
    String benBin = "";
    @Builder.Default
    boolean card = true;
    @Builder.Default
    boolean token = true;
    @Builder.Default
    String termId = "";
}
