package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxnDetailDTO {
    String trndT8;
    String drnme;
    String dracct;
    String dradd;
    String drcusid;
    String drbanK_NAME;
    String dramt;
    String drcur;
    String feeamt;
    String feevat;
    String crnme;
    String cracct;
    String cradd;
    String crcusid;
    String crbanK_NAME;
    String cramt;
    String crcur;
    String exrate;
    String remark;
}
