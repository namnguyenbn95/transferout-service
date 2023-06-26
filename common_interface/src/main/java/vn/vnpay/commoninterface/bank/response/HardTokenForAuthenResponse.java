package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.HardTokenAuthenMethodDTO;

import java.util.List;

@Getter
@Setter
public class HardTokenForAuthenResponse extends BaseBankResponse {
    private List<HardTokenAuthenMethodDTO> availableTokens;
}
