package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.ExchangeRateInquiryBankResponse;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class GetFeeTransferDTO {
    String accountPkgCode;
    String pkgCode;
    String promCode;
    String serviceCode;
    String authMethod;
    String ccy;
    BigDecimal amount;
    BigDecimal exchangeAmount;
    boolean isExamptVat;
    String creditAccount;
    String creditAccountAlias;
    ExchangeRateInquiryBankResponse tiGiaUsd;
    ExchangeRateInquiryBankResponse tiGiaNgoaiTe;
}
