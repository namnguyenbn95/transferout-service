package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EwalletUpdateCusBankRequest extends BaseBankRequest {
    //Mã nhà cung cấp
    private String partnerID;
    private String customerCode;
    private int cif;
    private String oldCustomerAccount;
    private String newCustomerAccount;
    private int newAccountBranch;
    private String newAccountCurrency;
    //mã maker
    private String tellerID;
    //Sequence unique của teller trong ngày
    private int tellerSequence;
    //Mã checker
    private String supID;
    //Sequence unique của maker trong ngày
    private int supSequence;

}

