package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dto.SmeCheckerServiceRoleDTO;
import vn.vnpay.dto.SmeMakerServiceRoleDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class GetListMakerCheckerChargebackResp {
    private List<SmeMakerServiceRoleDTO> listMaker;
    private List<SmeCheckerServiceRoleDTO> listChecker;
}
