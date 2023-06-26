package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomsTaxInfoDTO {
    private int cif;
    private boolean pay4Myself;
    private String customerName;
    private String customerAddress;
    private String payerTaxCode; // Mã số thuế người nộp thay
    private String payerName;
    private String payerAddress;
    private String issueNo; // Số quyết định
    private String taxCode;
    private String taxContent;
    private String tckn; // Tính chất khoản nộp
    private String cqtq; // Cơ quan thẩm quyền;
    private String kbnn;
    private TaxAccountDTO taxAccount;
    private RMKBNNInfoDTO rmKBNNInfo;
    private SoThueHqDTO soThueHq;
    private TransactionDataDTO transactionData;
}
