package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.FeeTransDetailChargebackDTO;
import vn.vnpay.commoninterface.dto.TransDetailChargebackDTO;

@Getter
@Setter
@Builder
public class CreateTransChargebackBankRequest extends BaseBankRequest {
    private String serviceCode;
    private String requestTSID;
    private String reasonID;
    private String departmentID;
    private String teller;
    private int sequence;
    private int cif;
    private String hostDate;
    private int tellerBrn;
    private String remark;
    private double amount;
    private String currency;
    private String cusAcct;
    private String cusFullName;
    private String cusCode;
    private String pcTime;
    private int type_request;
    private int is_auto_fee;
    private TransDetailChargebackDTO tsolTransaction;
    private FeeTransDetailChargebackDTO feeDataObject;
}
