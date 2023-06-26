package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CreditAccountDTO;
import vn.vnpay.commoninterface.dto.DebitAccountDTO;
import vn.vnpay.commoninterface.dto.FeeDTO;
import vn.vnpay.dbinterface.dto.DebitCardDTO;

@Getter
@Setter
@Builder
public class TransferOutBankRequest extends BaseBankRequest {
    private DebitAccountDTO debitAccount;
    private CreditAccountDTO creditAccount;
    private DebitCardDTO debitCard;
    private FeeDTO fee;

    // Số tiền VND
    private double amountVND;

    // Số tiền giao dịch nguyên tệ mà KH nhập
    private double originAmount;

    // Mã tiền giao dịch nguyên tệ
    private String originCurrency;

    // Nội dung giao dịch KH nhập
    private String content;

    // Remark giao dich
    private String remark;

    // TellerId
    private String tellerId;

    // TellerBranch
    private int tellerBranch;

    // Sequence
    private int sequence;

    // TraceId
    private int traceId;

    // PcTime HH24minss
    private String pcTime;

    private String hostDate;

    // Thông tin advice sử dụng để tạo điện vcb advice
    private String advice;

    private String TxnId;

    // route for napas
    Integer adviceRoute;

    private boolean isTranViaCard;
}
