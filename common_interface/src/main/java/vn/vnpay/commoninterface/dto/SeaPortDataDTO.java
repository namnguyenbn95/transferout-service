package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeaPortDataDTO {
    private String soCT;
    private String kyHieuCT;
    private String ngayCT;
    private String maDV;
    private String tenDV;
    private String chuongNS;
    private String tenChuongNS;
    private String tieuMuc;
    private String tenTieuMuc;
    private String diaChi;
    private String maLoaiPhi;
    private String tenLoaiPhi;
    private String maDVThuPhi;
    private String tenDVThuPhi;
    private String maCQTDVThuPhi;
    private String tenCQTDVThuPhi;
    private String soTKHQ;
    private String maLH;
    private String ngayTKHQ;
    private String maHQ;
    private String soTKNP;
    private String ngayTKNP;
    private String tkkb;
    private String tenTKKB;
    private String maKBTH;
    private String maKB;
    private String tenKbct;
    private String tenKB;
    private Integer soTienTo;
    private String dienGiai;
    private List<SeaPortRecordDTO> thongTinNopTien;
}
