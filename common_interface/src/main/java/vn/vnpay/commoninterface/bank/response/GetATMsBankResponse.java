package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ATMsDTO;

import java.util.List;

@Getter
@Setter
public class GetATMsBankResponse extends BaseBankResponse {
    private List<ATMsDTO> data;
}
