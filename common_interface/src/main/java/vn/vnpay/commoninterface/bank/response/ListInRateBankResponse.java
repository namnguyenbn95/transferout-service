package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListInRateDTO;

import java.util.List;

@Getter
@Setter
public class ListInRateBankResponse extends BaseBankResponse {
    private List<ListInRateDTO> data;
}
