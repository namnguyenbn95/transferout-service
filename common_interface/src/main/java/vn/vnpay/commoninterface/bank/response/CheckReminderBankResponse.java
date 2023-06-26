package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.NotiObjectDTO;

import java.util.List;

@Getter
@Setter
public class CheckReminderBankResponse extends BaseBankResponse {
    private List<NotiObjectDTO> lstNotiObject;
}
