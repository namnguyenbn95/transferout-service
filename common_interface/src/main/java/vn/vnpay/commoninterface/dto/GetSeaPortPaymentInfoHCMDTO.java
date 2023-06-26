package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetSeaPortPaymentInfoHCMDTO {
    private int cif;
    private String taxCode;
    private boolean pay4Myself;
    private String maLoaiPhi;
    private String maDVThuPhi;
    private String taxPayerCode;
    private String tenDV;
    private String maDV;
    private String soChungTu;
    private SeaPortAccountDataHCMDTO seaPortAccountData;
    private List<SeaPortRecordHCMDTO> thongTinNopTien;
    private TransactionDataDTO transaction;
}
