package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CardTDQTDTO;
import vn.vnpay.commoninterface.dto.VipAccDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class GetFeeComboBankReq extends BaseBankRequest {
    private String cif;
}
