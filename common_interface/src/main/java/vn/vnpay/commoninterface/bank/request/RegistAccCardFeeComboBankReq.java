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
public class RegistAccCardFeeComboBankReq extends BaseBankRequest {
    private String cif;
    private String creator;
    private String creationTime;
    private String approver;
    private String approveTime;
    private String brn;
    private boolean isUpdateAccount;
    private boolean isUpdateCardTDQT;
    private boolean isUpdateCardGNQT;
    private List<VipAccDTO> vipAccList;

    private List<CardTDQTDTO> cardTDQTList;
    private List<CardTDQTDTO> cardGNQTList;
}
