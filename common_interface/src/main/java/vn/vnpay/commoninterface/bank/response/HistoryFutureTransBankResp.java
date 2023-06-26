package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.ListFutureTransDTO;

import java.util.List;

@Getter
@Setter
public class HistoryFutureTransBankResp extends BaseBankResponse {
    private List<ListFutureTransDTO> listTransDetail;
    private String totalPage;
    private String totalRows;
}
