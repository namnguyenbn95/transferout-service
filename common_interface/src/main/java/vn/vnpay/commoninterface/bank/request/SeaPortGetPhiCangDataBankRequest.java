package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeaPortGetPhiCangDataBankRequest extends BaseBankRequest {
    private int cif;
    private String soChungTu;
    private String maLoaiPhi;
    private String maDonViThuPhi;
    private String ngayChungTu;
    private int hinhThucNop;
    private String maSoThue;
}
