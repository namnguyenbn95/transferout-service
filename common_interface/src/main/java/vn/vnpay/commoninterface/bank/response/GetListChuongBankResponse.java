package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ChuongDTO;

import java.util.List;

@Getter
@Setter
public class GetListChuongBankResponse extends BaseBankResponse {
    private List<ChuongDTO> listChuong;
}
