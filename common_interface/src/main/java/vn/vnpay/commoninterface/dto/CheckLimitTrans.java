package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckLimitTrans {
    private String ccy;
    private Double amount;

    public CheckLimitTrans(String ccy, Double amount) {
        this.ccy = ccy;
        this.amount = amount;
    }
}
