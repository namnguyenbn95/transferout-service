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
public class TransferGLFeeBankRequest extends BaseBankRequest {
    private DebitAccountDTO debitAccount;
    private CreditAccountDTO creditAccount;
    private DebitCardDTO debitCard;
    private FeeDTO fee;

    // Số tiền VND
    private double amountVND;

    // Nội dung giao dịch KH nhập
    private String content;

    // Số tiền giao dịch nguyên tệ mà KH nhập
    private double originAmount;

    // Mã tiền giao dịch nguyên tệ
    private String originCurrency;

    // Remark giao dich
    private String remark;

    // TellerId
    private String tellerId;

    // TellerBranch
    private int tellerBranch;

    // Sequence
    private int sequence;

    // PcTime HH24minss
    private String pcTime;

    private String txnType; // Loại giao dịch chuyển tiền hoặc revert (FT = chuyển tiền, RFT = revert)

    private int orgSequence;

    private String orgPcTime;

    // TraceId
    private int traceId;


}
