package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThongTinThueDTO {
    private String maNoiDungKinhTe;
    private String tenNoiDungKinhTe;
    private Double soTienNop;
    private String kyHanThue;
    private String maChuong;
    private String tenChuong;
    private String maCqThu;
    private String maCqThue;
    private String tenCqThue;
    private String soTkCo;
    private String tenTkCo;
    private String maDBHC;
    private String soQuyetDinh;
    private String ngayQuyetDinh;
    private String desc;
}
