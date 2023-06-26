package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaxPaymentInfoDTO {
    private int cif;
    private String taxCode;
    private boolean pay4Myself;
    private String customerName;
    private String customerAddress;

    // MST người nộp thay
    private String taxPayerCode;

    // Tên người nộp thay
    private String payerName;

    // Địa chỉ người nộp thay
    private String payerAddress;

    private String issueNo;
    private String issueDate;
    private String taxContent;
    private RawTaxBookDTO rawTaxBook;
    private List<TaxBookRecordDTO> taxBookRecord;   // Các khoản thuế sẽ nộp
    private TaxAccountDTO taxAccount;
    private boolean isIncomeTax;
    private String tckn;    // Tính chất khoản nộp
    private String cqtq;    // Cơ quan thẩm quyền
    private RMKBNNInfoDTO rmKBNNInfo;
    private String maChuong;
    private String maCoQuanThu;
    private List<ThongTinThueDTO> listThongTinThue; // Thông tin từ TCT có những khoản nộp nào
    private String tenKBNNChiTiet;
    private TransactionDataDTO transactionData;
}
