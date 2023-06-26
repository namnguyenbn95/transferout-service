package vn.vnpay.commoninterface.bank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BHXHGetDataBankRequest extends BaseBankRequest {
    private int cif;
    private String cityCode;
    private String loaiHinhThu;
    private String cqBHXH;
    private String maCQThu;
    private String maDoiTuong;
    private String siNumber;
    private String bhytCode;
    private Integer month;
}
