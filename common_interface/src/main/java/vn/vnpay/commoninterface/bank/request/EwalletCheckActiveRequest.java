package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.EwalletFieldDTO;

import java.util.ArrayList;

@Getter
@Setter
public class EwalletCheckActiveRequest extends BaseBankRequest {
    //Mã nhà cung cấp
    private String partnerID;
    private String customerCode;
    private String customerAccount;
    private int cif;
    //mã maker
    private String tellerID;

    //Sequence unique của teller trong ngày
    private int tellerSequence;
    //Lựa chọn loại nhà cung cấp có cho cashin tự động hay không
    private boolean registerAutoCashin;

    //Note: chỉ có khi RegisterAutoCashin = true .
    // Danh sách các trường thông tin động của nhà cung cấp
    private ArrayList<EwalletFieldDTO> chechActiveFields;

}
