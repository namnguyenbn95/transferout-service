package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxAccountDTO {
    private String maCQThu;
    private String maCQThue;
    private String tenCQThu;
    private String maKBac;
    private String tenKBac;
    private String kbAccount;
    private String kbBankCode;
    private String bankCode;
    private String bankName;
}
