package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListFutureTransDTO;

import java.util.List;

@Getter
@Setter
public class ListFutureTransBankResponse extends BaseBankResponse {
    private List<ListFutureTransDTO> listTransDetail;
}
