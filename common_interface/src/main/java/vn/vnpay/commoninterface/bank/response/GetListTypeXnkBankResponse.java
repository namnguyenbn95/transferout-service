package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.TypeXnkDTO;

import java.util.List;

@Getter
@Setter
public class GetListTypeXnkBankResponse extends BaseBankResponse {
    private List<TypeXnkDTO> listTypeXnk;
}
