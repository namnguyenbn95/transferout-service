package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetCardTokenDTO;

import java.util.List;

@Getter
@Setter
public class GetCardTokenBankResponse {
    private String code;
    private String message;
    private List<GetCardTokenDTO> vcbTokenInfors;
}
