package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListFutureTransBankRequest extends BaseBankRequest {
    private String userId;
    private String username;
    private Integer cif;
    private String startDate;
    private String endDate;
    private String makerId;
    private String checkerId;
}
