package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashFlowDateDTO {
    String dateValue;
    double dudau;
    double duCuoiNgay;
    double namNo;
    double namCo;
    double thangNo;
    double thangCo;
    double ngayNo;
    double ngayCo;
    double tuanNo;
    double tuanCo;
    boolean isDefault;
}
