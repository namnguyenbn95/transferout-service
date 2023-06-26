package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetSeaPortPaymentInfoDTO {
    private int cif;
    private String taxCode;
    private boolean pay4Myself;
    private String maLoaiPhi;
    private String maDVThuPhi;
    private String taxPayerCode;
    private String soCT_TBP;    // Số chứng từ
    private String kyHieuCT_TBP;    // Ký hiệu chứng từ

    // example: 2021-07-08
    private String ngayCT_TBP;
    private SeaPortAccountDataDTO seaPortAccountData;
    private SeaPortDataDTO seaPortData;
    private List<Message321DataTTCT_NPDTO> message321;
    private RMKBNNInfoDTO rmKBNNInfo;
    private TransactionDataDTO transaction;
    private String content;
    private String maTieuMuc;
}
