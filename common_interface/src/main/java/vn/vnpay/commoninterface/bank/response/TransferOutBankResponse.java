package vn.vnpay.commoninterface.bank.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RmDataDTO;

@Getter
@Setter
@Builder
public class TransferOutBankResponse extends BaseBankResponse {
    private int sequence;
    private String hostDate;
    private String msgID;
    private RmDataDTO rmData; //data bank response
}
