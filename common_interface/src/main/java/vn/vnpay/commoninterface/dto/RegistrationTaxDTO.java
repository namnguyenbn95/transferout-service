package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistrationTaxDTO {
    private int cif;
    private boolean pay4Myself;
    private String issueNo;
    private String taxCode;
    private String taxContent;
    private String regisFeeType;
    private String customerName;
    private String customerAddress;
    private String payerName;
    private String payerAddress;
    private String payerTaxCode;
    private String tckn;
    private String cqtq;
    private String kbnn;
    private String tinh;
    private String huyen;
    private String xa;
    private String provinceCode;
    private String noiDungKhoanNop;
    private boolean isPaymentHCC;
    private List<InfoFeeDataDTO> infoFeeData;
    private List<RegistrationFeeRecordDTO> listRegistrationFeeRecord;
    private TaxAccountDTO taxAccount;
    private RMKBNNInfoDTO rmKBNNInfo;
    private TransactionDataDTO transactionData;
    private String content;
    private String dacDiemPT;
    private String tenKbac;
}
