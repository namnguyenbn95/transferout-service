package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.HaiQuanDTO;

import java.util.List;

@Getter
@Setter
public class GetListHqBankResponse extends BaseBankResponse {
    private List<HaiQuanDTO> listKbth;
}
