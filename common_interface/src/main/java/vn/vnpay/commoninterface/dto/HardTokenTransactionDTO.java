package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HardTokenTransactionDTO {
    private String transactionId;
    private Double transactionAmount;
    private String transactionCurrency;
    private String transactionDetail;
    private String transactionType;
}
