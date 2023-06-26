package vn.vnpay.commoninterface.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.SmeTrans;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BaseTransactionResponse {
    private String tranDate;
    private String tranToken; // Token xác thực
    private String tranxId;
    private String authenType; // Loại phương thức xác thực
    private String dataAuthen; // Dữ liệu soft otp
    private String serviceCode;
    private String source;
    private String confirmType; // Loại giao dịch xác thực 1 tay, 2 tay, 3 tay...
    private String curCode;
    private double fee;
    private BigDecimal exchangeFee;
    private double vat;
    private BigDecimal exchangeVat;
    private BigDecimal feeU;
    private BigDecimal vatU;
    private BigDecimal totalFeeU;
    private double totalFee;
    private BigDecimal exchangeTotalFee;
    private double totalAmount;
    private long exchangeTotalAmount;
    private double amount;
    private long exchangeAmount;
    private String toAccName;
    private String isExecTrans;
    private String transContent;
    private String feeToShow;
    private String vatToShow;
    private String totalFeeToShow;

    private boolean isContact;

    private long exchangeRate;

    // billing response
    String cusName;
    String addInfo;
    String sourceAccountName;
    String remark;
    String invoiceNo;
    String billServiceName;
    String billProviderName;
    String billSubProviderName;
    String billServiceNameEn;
    String billProviderNameEn;
    String billSubProviderNameEn;
    String cusRefCode;

    // client info
    String clientRqCcy;

    // for trans batch
    private String fromAccount;
    private String toAccount;

    // Card
    private String custName;
    private String acctNbr;
    private String crdNbr;
    private String crdBlk;
    private String custAddr1;
    private String custAddr2;
    private String custCity;
    private String custPhone;
    private String custNbr;
    private String custBank;
    private String custType;
    private String custStat;
    private String corpCif;
    private String crddtOpen;
    private String crdBlkDte;
    private String crdPdtNbr;
    private String crdExpDte;
    private String crdAutUsg;
    private String acctStat;
    private String productDesc;
    private String supRel;
    private boolean isDebit;
    private String cardName;
    private String cardFormUrl;

    //accountNo Select
    private String accountNoSelect;
    private String accBranchName;
    private String accBranchAddr;
    private double promotionValue;
    private String effectiveDate;
    private List<SmeTrans> batchTrans;
    private Integer batchSize;
    private String batchId;
    private List<String> listServiceCodes;

    //loan payment
    private double prepayPenalty;       // phí trả nợ trước hạn
    private double prepayPenaltyOrigin; //phí trả nợ trước hạn theo ccy tk trích nợ

    //tra soat
    private String tsolRef;             // mã yêu cầu tra soát
}