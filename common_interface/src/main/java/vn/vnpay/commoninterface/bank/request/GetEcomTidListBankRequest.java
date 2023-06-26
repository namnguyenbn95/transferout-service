package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetEcomTidListBankRequest extends BaseBankRequest {
    private String securitiesID;
}
