package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitCustomerInfo;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;

import java.util.ArrayList;

@Getter
@Setter
public class ListAutoDebitUserResponse extends BaseBankResponse {
    ArrayList<AutoDebitCustomerInfo> autoDebitUserList;
}
