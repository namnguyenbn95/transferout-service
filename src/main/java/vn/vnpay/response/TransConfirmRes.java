package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransConfirmRes {
    private String tranxId;
    private String tranxDate;
    private double fee;
    private double exchangeFee;
    private double vat;
    private double exchangeVat;
    private double totalFee;
    private double exchangeTotalFee;
    private double totalAmount;
    private double exchangeTotalAmount;
    private double amount;
    private double exchangeAmount;
    private String isExecTrans;
    private boolean isContact;
    private double exchangeRate;
}
