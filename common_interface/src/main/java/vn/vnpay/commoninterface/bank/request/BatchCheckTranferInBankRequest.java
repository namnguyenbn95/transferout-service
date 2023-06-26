package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchCheckTranferInBankRequest extends BaseBankRequest {
    List<String> accountNoList;
}
