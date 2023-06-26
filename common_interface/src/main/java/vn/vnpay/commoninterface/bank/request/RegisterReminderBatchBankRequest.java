package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.NotiObjectDTO;

import java.util.List;

@Getter
@Setter
public class RegisterReminderBatchBankRequest extends BaseBankRequest {
    private int cif;
    private String custype;
    private String source;
    private String teller;
    private String sequence;
    private String sup;
    private String supSequence;
    private List<NotiObjectDTO> lstNotiObject;

}
