package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HistoryFutureTransBankReq extends BaseBankRequest {
    String transId;
    String makerId;
    String checkerId;
    String cif;
    String transType;
    String creditChannel;
    String procStatus;
    String debitAccount;
    String currency;
    String transTellerId;
    String startDate;
    String endDate;
    int pageIndex;
    int pageSize;
}
