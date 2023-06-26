package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.KbthDTO;

import java.util.List;

@Getter
@Setter
public class GetListKbthBankResponse extends BaseBankResponse {
    private List<KbthDTO> listKbth;
}
