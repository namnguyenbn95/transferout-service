package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListTransChargebackRespClientDTO {
    private String createdDate;
    private String tsolRef;
    private double amount;
    private String ccy;
    private String status;
    private String requestTSName;                 //tên yêu cầu
    private String requestTSName_EN;              //tên yêu cầu tiếng anh
}
