package vn.vnpay.commoninterface.response.softotp;

import lombok.Data;

@Data
public class CancelSoftResponse {
    private String code;
    private String message;
    private String data;
}
