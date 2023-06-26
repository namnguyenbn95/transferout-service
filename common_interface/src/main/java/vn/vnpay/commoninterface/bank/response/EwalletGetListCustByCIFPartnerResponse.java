package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.EwalletCustomerDTO;

import java.util.ArrayList;

@Getter
@Setter
public class EwalletGetListCustByCIFPartnerResponse extends BaseBankResponse {
    ArrayList<EwalletCustomerDTO> listEwalletCustomerObj;
}
