package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DMPhiDTO;

import java.util.List;

@Getter
@Setter
public class SeaPortGetDMPhiBankResponse extends BaseBankResponse {
    List<DMPhiDTO> listDMPhi;
}
