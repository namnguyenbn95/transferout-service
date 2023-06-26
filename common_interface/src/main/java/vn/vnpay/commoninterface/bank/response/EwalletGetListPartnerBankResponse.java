package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.EwalletCategoryDTO;

import java.util.ArrayList;

@Getter
@Setter
public class EwalletGetListPartnerBankResponse extends BaseBankResponse {
    ArrayList<EwalletCategoryDTO> listEwalletCategoryObj;
}
