package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import java.math.BigDecimal;

@Getter
@Setter
public class GetDetailTransChargebackRequest extends BaseClientRequest {
    // Ngày giao dịch
    // example: 2020-06-02
    private String transactionDate;

    // Số tham chiếu
    // example: 5078 - 61762
    private String reference;

    // Loại giao dịch, C = ghi có, D = trích nợ
    // example: C
    private String cd;

    // Số tiền giao dịch
    // example: 50005
    private BigDecimal amount;

    // Remark giao dịch
    // example: MBVCB.6804250.VCBQRCODE.IN.FRT.147852369
    private String description;

    // TranCode giao dịch
    // example: 34
    private String tranCode;

    // TranType giao dịch
    // example: TRF
    private String tranType;

    // PCTime giao dịch
    // example:101829
    private String pcTime;

    // Print sao ke
    private String effectDate;
}
