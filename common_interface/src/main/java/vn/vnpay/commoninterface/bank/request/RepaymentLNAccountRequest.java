package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RepaymentLNAccountRequest extends BaseBankRequest {
    private String fromAcctNo;                   // stk trích nợ
    private String fromAcctType;                 // loại tk trích nợ
    private String fromOldAcctNo;               // tk alias trích nợ
    private String fromOldAcctType;             // Loại TK alias trích nợ
    private String toAcctNo;                    // STK ghi có
    private String toAcctType;                  // Loại TK ghi có
    private String toOldAcctNo;                 // STK alias ghi có
    private String toOldAcctType;               // Loại TK alias ghi có
    private double fromAmount;                  // Số tiền trích nợ
    private String fromAmountCurr;              // Loại tiền trích nợ
    private double fromLCEAmount;               // Số tiền trích nợ quy đổi VND. Không bắt buộc nếu loại tiền trích nợ là VND
    private double fromExchangeRate;            // Tỷ giá quy đổi. Không bắt buộc nếu loại tiền trích nợ là VND
    private double toAmount;                    // Số tiền ghi có
    private String toAmountCurr;                // Loại tiền ghi có
    private double toLCEAmount;                 // Số tiền ghi có quy đổi VND, không bắt buộc nếu loại tiền trích nợ là VND
    private double toExchangeRate;              // Tỷ giá quy đổi. Không bắt buộc nếu loại tiền trích nợ là VND
    private String feeChargeType;               // Loại phí
    private double feeAmount;                   // Số tiền phí chưa VAT
    private String feeAmountCurr;               // Mã tiền tệ phí chưa VAT
    private double feeLCEAmount;                // Số tiền phí quy đổi VND
    private double feeVATAmount;                // Số tiền phí VAT
    private String feeVATAmountCurr;            // Mã tiền tệ phí VAT
    private double feeVATLCEAmount;             // Số tiền phí VAT quy đổi VND
    private String effDate;                     // Ngày hiệu lực. Format YYYY-MM-DD
    private String trnTime;                     // Thời gian GD. Format HHMMSS
    private String remark;                      // remark giao dịch
    private String transType;                   // loại hạch toán
}
