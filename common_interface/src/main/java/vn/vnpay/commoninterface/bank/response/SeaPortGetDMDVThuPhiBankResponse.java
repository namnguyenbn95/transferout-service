package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.DMDVThuPhiDTO;

import java.util.List;

@Getter
@Setter
public class SeaPortGetDMDVThuPhiBankResponse extends BaseBankResponse {
    private List<DMDVThuPhiDTO> listDMDVThuPhi;
}
