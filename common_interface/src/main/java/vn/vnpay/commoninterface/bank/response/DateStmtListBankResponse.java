package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DateStmtListBankResponse extends BaseBankResponse {
    List<String> recordDateList;
}
