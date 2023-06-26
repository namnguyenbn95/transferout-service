package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TraCuuThueHqBankRequest extends BaseBankRequest {
    private int cif;
    private int paymentType;    // 1: Tu nop; 2: Nop thay
    private String maDV;
    private String namDK;
    private String soTK;
}
