package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardStatementInquiryDTO;

@Getter
@Setter
public class CardStatementInquiryBankResponse extends BaseBankResponse {
    private CardStatementInquiryDTO cardStatementInquiry;
}
