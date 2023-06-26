package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.FeeComboDTO;

import java.util.List;

@Getter
@Setter
public class GetListFeeCodeComBoBankResponse extends BaseBankResponse {
   List<FeeComboDTO> feeComboList;
}
