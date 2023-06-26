package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusInquiryBankResponse extends BaseBankResponse {
    // Trạng thái join account (Y = là join account, khác Y thì không là join account)
    private String jointSts;

    // Trạng thái WrongSigSts
    private String wrongSigSts;

    // Trạng thái PostingSts
    private String postingSts;

    // Trạng thái chặn ghi có (Y = chặn ghi có, khác Y = không chặn)
    private String creditSts;

    // Trạng thái chặn trích nợ (Y = chặn trích nợ, khác Y = không chặn)
    private String debitSts;

    // Trạng thái chặn FD FDBlockedSts
    private String fdBlockedSts;

    // Trạng thái chặn FD FDLostSts
    private String fdLostSts;

    // Trạng thái chặn FD FDUnderLienSts
    private String fdUnderLienSts;

    // MsgID esb
    private String msgID;

    // MsgDetail esb
    private String msgDetail;
}
