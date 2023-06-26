package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RelationShipObjectDTO;
import vn.vnpay.commoninterface.dto.TransDetailChargebackDTO;

@Getter
@Setter
public class GetTransChargebackInfoBankResponse extends BaseBankResponse {
    private String serviceCode;             //Mã loại giao dịch tra soát
    private String serviceName;             //Tên loại giao dịch tra soát
    private String serviceName_EN;          //Tên loại giao dịch tra soát tiếng anh
    private String pcTime;
    private TransDetailChargebackDTO transDetail;
    private RelationShipObjectDTO relationshipObj;
}
