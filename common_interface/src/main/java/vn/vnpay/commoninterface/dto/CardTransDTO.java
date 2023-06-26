package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardTransDTO {
    // So Tien Thanh Toan
    private String txnAmt;

    // So Tien Giao Dich Goc
    private String orgAmt;

    // Ngay Giao Dich
    private String trnDate;

    // TranCode cua giao dich
    private String trnCode;

    // Ngay He Thong
    private String postingDate;

    // Ma Ngoai Te
    private String trnCurrCode;

    // Ghi Chu (Remark)
    private String txnDesc;

    // So Tham Chieu
    private String trnRef;

    // So tien ghi no
    private String debitAmt;

    // So tien ghi co
    private String creditAmt;
}
