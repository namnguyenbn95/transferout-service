package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.BHXHCompanyDTO;

import java.util.List;

@Getter
@Setter
public class BHXHGetCompanyBankResponse extends BaseBankResponse {
    private List<BHXHCompanyDTO> listCompany;
}
