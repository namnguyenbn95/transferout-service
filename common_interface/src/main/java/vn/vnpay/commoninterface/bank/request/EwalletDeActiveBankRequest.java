package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletDeActiveBankRequest extends BaseBankRequest {
    //Mã nhà cung cấp
    private String partnerID;
    private String customerCode;
    private int cif;
    //mã maker
    private String tellerID;
    //Sequence unique của teller trong ngày
    private int tellerSequence;
    //Mã checker
    private String supID;
    //Sequence unique của maker trong ngày
    private int supSequence;
}
