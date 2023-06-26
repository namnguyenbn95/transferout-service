package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetCumulativeBalByDateBankRequest extends BaseBankRequest {
    private String accountNewNo;
    private String accountType;
    private String dateSearch;

}
