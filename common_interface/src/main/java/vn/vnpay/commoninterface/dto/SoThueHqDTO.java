package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SoThueHqDTO {
    private String maCuc;
    private String tenCuc;
    private String maHQPH;
    private String tenHQPH;
    private String maHQCQT;
    private String maDV;
    private String tenDV;
    private String maChuong;
    private String maHQ;
    private String tenHQ;
    private String maLH;
    private String tenLH;
    private Integer namDK;
    private String soTK;
    private String clientRqSoTK;
    private String maNTK;
    private String tenNTK;
    private Integer maLT;
    private String tenLT;
    private Integer maHTVCHH;
    private String tenHTVCHH;
    private String ngayDK;
    private String maKB;
    private String tenKB;
    private String tkkb;
    private Integer ttNo;
    private String tenTTN;
    private Integer ttNoCT;
    private String tenTTNVT;
    private Integer soCtNo;
    private Double duNoTO;
    private List<CtNoDTO> listCtNo;
}
