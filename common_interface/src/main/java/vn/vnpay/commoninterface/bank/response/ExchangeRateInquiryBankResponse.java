package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExchangeRateInquiryBankResponse extends BaseBankResponse {
    // Tỉ giá mua vào
    private BigDecimal buyRate;

    // Tỉ giá bán ra
    private BigDecimal sellRate;

    // Tỉ giá trung bình
    private BigDecimal midRate;

    private BigDecimal appXferBuy;

    private String msgID;
}
