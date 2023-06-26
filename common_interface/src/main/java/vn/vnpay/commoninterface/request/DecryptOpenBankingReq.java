package vn.vnpay.commoninterface.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ContextOpenBankingDTO;
import vn.vnpay.commoninterface.dto.PayloadEncryptOpenBankingDTO;

@Getter
@Setter
@Builder
public class DecryptOpenBankingReq {
    private ContextOpenBankingDTO context;
    private PayloadEncryptOpenBankingDTO payload;
}
