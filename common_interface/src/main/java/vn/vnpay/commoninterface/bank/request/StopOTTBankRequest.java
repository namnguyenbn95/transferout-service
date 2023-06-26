package vn.vnpay.commoninterface.bank.request;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StopOTTBankRequest extends BaseBankRequest {
    private String cif;
    @SerializedName("phonenumber")
    private String phoneNumber;
}
