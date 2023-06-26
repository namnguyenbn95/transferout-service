package vn.vnpay.commoninterface.bank.response;

import lombok.Data;
import vn.vnpay.commoninterface.config.PostProcessingEnabler;

import java.io.Serializable;

@Data
public class ResponseStatus implements Serializable, PostProcessingEnabler.PostProcessable {
    private String resCode;
    private String resMessage;
    private String traceNo;
    private Boolean isSuccess;
    private Boolean isFail;
    private Boolean isTimeout;

    @Override
    public void postProcess() {
//        if (resCode != null) {
//            if (resCode.equals("0") || resCode.equals("00")) {
//                return;
//            }
//            resCode = "BANK-" + resCode;
//        }
    }
}
