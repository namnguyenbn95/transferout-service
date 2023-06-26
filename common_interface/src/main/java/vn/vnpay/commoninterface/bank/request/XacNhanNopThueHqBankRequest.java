package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CustomsTaxInfoDTO;

@Getter
@Setter
@Builder
public class XacNhanNopThueHqBankRequest extends BaseBankRequest {
    private int cif;
    private CustomsTaxInfoDTO customsTaxInfo;
}
