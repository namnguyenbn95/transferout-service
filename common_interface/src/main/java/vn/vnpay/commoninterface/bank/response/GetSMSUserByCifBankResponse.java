package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.SmsUserDTO;

import java.util.List;

@Getter
@Setter
public class GetSMSUserByCifBankResponse extends BaseBankResponse {
    private List<SmsUserDTO> listSMSUser;
}
