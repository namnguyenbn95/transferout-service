package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxBookRecordDTO {
    private String soQuyetDinh;
    private String ngayQuyetDinh;
    private String maChuong;
    private String tenChuong;
    private String maMuc;
    private String tenMuc;
    private String maTmuc;
    private String tenTmuc;
    private String maCqThu;
    private String maCqThue;
    private String tenCqThue;
    private String soTkCo;
    private String tenTkCo;
    private Double soTien;
    private String kyThue;
    private String provinceCode;
}
