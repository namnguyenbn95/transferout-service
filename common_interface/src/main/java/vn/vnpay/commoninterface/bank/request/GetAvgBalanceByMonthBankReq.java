package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetAvgBalanceByMonthBankReq extends BaseBankRequest{
    private String cif;
    private String account;
    private String month;
}

