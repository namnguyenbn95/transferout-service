package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CtNoDTO {
    private String maHQ;
    private String tenHQ;
    private String maLH;
    private String tenLH;
    private String namDK;
    private String soTK;
    private String loaiThue;
    private String tenLoaiThue; // for client
    private String taxTypeCode; // for client
    private String khoan;
    private String tieuMuc;
    private Double duNo;
}
