package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DbhcDTO;

import java.util.List;

@Getter
@Setter
public class GetListDbhcBankResponse extends BaseBankResponse {
    private List<DbhcDTO> listDbhc;
}
