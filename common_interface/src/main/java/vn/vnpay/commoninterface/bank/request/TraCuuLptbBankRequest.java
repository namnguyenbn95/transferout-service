package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TraCuuLptbBankRequest extends BaseBankRequest {
    private int cif;
    private String taxCode;
    private String issueNo;     // So quyet dinh
    private int regisFeeType;   // Loai hinh nop: 1 - thue o to, xe may, 3 - thue nha dat
    private int paymentType;    // 1 - tu nop, 2 - nop thay
}
