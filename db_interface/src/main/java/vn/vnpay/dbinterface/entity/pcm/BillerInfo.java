package vn.vnpay.dbinterface.entity.pcm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BillerInfo {
    String billerID;
    String billerName;
    String billerVnName;
    String aggregatorID;
    ArrayList<SvcType> svcTypeList;
    String isBillerDivision;
    String billerDivisionCaptureMode;
    String billPaymentSystem;
}
