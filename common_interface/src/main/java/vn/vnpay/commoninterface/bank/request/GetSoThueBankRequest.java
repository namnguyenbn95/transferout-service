package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetSoThueBankRequest extends BaseBankRequest {
    private int cif;
    private String taxCode;
    private int paymentType;    // 1 - Tu nop; 2 - Nop thay
}
