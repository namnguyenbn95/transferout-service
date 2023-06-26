package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListExRateDTO;

import java.util.List;

@Getter
@Setter
public class ListExRateBankResponse extends BaseBankResponse {
    private List<ListExRateDTO> data;
}
