package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.GetLNAccountListDTO;

import java.util.List;

@Getter
@Setter
public class GetLNAccountListResponse extends BaseBankResponse {
    private String msgID;
    private String msgDetail;
    private List<GetLNAccountListDTO> lnAccountList;
}
