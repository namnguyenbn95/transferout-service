package vn.vnpay.commoninterface.bank.response.pcm;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.response.BaseBankResponse;
import vn.vnpay.dbinterface.entity.pcm.BillerInfo;

import java.util.ArrayList;

@Getter
@Setter
public class PcmListBillerResponse extends BaseBankResponse {
    ArrayList<BillerInfo> billerInfoRec;
}
