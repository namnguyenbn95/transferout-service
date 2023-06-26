package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.bank.entity.billing.AutoDebitBillingInfo;
import vn.vnpay.commoninterface.bank.request.CardStatementUpdateBankRequest;
import vn.vnpay.commoninterface.bank.request.RepaymentLNAccountRequest;
import vn.vnpay.commoninterface.bank.response.GetFeeLNRepaymentResponse;
import vn.vnpay.commoninterface.bank.request.CreateTransChargebackBankRequest;
import vn.vnpay.dbinterface.dto.CardDTO;
import vn.vnpay.dbinterface.dto.DebitCardDTO;
import vn.vnpay.dbinterface.entity.SmeTrans;
import vn.vnpay.dbinterface.entity.pcm.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Builder
public class TransactionMetaDataDTO {
    private String beneBankCode;
    private String beneBankName;
    private String beneCityCode;
    private String beneCityName;
    private String beneBranchCode;
    private String beneBranchName;

    private DebitAccountDTO debitAccount;
    private CreditAccountDTO creditAccount;
    private DebitCardDTO debitCard;
    private RecipientDTO recipient;
    private BatchDataDTO batchData;
    private FeeDTO fee;
    private CronJobDTO cronJob;
    private FutureTransDataDTO futureTransData;
    private double amountVND;
    private double originAmount;

    private String futureDate;
    private String fromDate;
    private String toDate;
    private int interval;
    @Builder.Default
    private String intervalUnit = "0"; // 1: Ngày; 2: Tuần; 3: Tháng
    @Builder.Default
    private boolean isExecTrans = false;
    private long makerId;
    private long checkerId;

    // transfer via card
    private String debitAccountNo;
    private boolean isTranViaCard;

    // Check credit VRA
    private boolean isCreditVRA;
    private String vaName;
    private String vaStatus;
    private String vrAccount;
    private String realAccountName;
    private String realAccountNumber;

    // route for napas
    Integer adviceRoute;
    String routeName;
    String accountPosting;
    String accountPostingType;

    private double totalFee;
    private double exchangeTotalFee;

    private CheckLimitTrans checkLimitTrans;

    // billing expand data
    String companyCode;
    String vcbCode;
    String cusName;
    String addInfo;
    String sourceAccountName;
    SvcIdent svcIdent;
    ArrayList<BillRec> billRec;
    ArrayList<AddlFieldMetaData> addlFieldMetaData;
    String cusRefCode;
    String billServiceCode;
    String billServiceName;
    String billServiceNameEn;
    String billProviderCode;
    String billProviderName;
    String billProviderNameEn;
    String billSubProviderCode;
    String billSubProviderName;
    String billSubProviderNameEn;
    HashMap<String, String> clientMapField;
    String service2CheckRole;
    PayerInfo payerInfo;
    ArrayList<BillField> billField;

    String remark;
    String creditAdviceFlag;
    String directCreditFlg;
    String billerAcctNo;
    String creditAccSubBillProvider;
    String providerAcc;

    // Tax data
    private TaxPaymentInfoDTO taxPaymentInfo;
    private RegistrationTaxDTO registrationTax;
    private CustomsTaxInfoDTO customsTaxInfo;
    private String chargeType;     // 1: Nộp thuế; 2: Nộp lệ phí

    // BHXH Data
    private BHXHDataDTO bhxhData;

    // Ha tang cang bien
    private GetSeaPortPaymentInfoDTO seaportPaymentInfo;
    private GetSeaPortPaymentInfoHCMDTO seaportPaymentInfoHCM;

    // phí USD
    private BigDecimal feeU;
    private BigDecimal vatU;

    private String tellerId;
    private int sequence;
    private String hostDate;
    private String pcTime;

    // client info
    private String clientRqCcy;

    private SmeTrans orgTran;
    private String makerEmail;

    // batch file data
    private String paymentGoal;

    // In giao dich
    private String debitName;
    private String debitAddr;
    private String creditAddr;

    // Thẻ
    private CardStatementUpdateBankRequest creditCardPayment;
    private CardDTO cardDto;
    private String recordDate;

    // autodebit
    private AutoDebitBillingInfo adBillingInfo;
    private TransBatchDTO transBatchDTO;
    private String cif;

    //account select
    private AccountSelectDTO accSelect;

    private String idIssuedPlace;
    
    private String sigCustType;

    private String cardMaskingNumber;

    private String accPkgCode;

    private String vatExamptFlag;

    private String batchFileRefNo;

    //add loan payment
    private RepaymentLNAccountRequest repaymentLNAccRequest;
    private GetFeeLNRepaymentResponse feeLNRepaymentResponse;

    private String rmFwBranch;
    private String facilityNo;      //số hợp đồng
    private Double prepayPenalty;   //phí trả nợ trước hạn

    private String statusChargeback;  //trang thai yeu cau tra soat 1-da gui yeu cau tra soat
    private String statusCreateChargeback;  //0-giao dịch đã được lập lệnh tra soát, 1-giao dịch đã được tạo qua bank thành công
    private Double prepayPenaltyOrigin;   //phí trả nợ trước hạn theo ccy tài khoản trích nợ
    private String typePaymentLoan;  //Hình thức thanh toán 1:dư nợ gốc hiện tại; 2:dư nợ gỗ đến hạn; 3:số tiền khác
    private double originDebitAmt;

    private CreateTransChargebackBankRequest createTransChargebackBankRequest;
    private String tsoLRef;  //mã yêu cầu tra soát
    private String requestTSID;     //loại yêu cầu tra soát
    private String requestTSName;                 //tên yêu cầu
    private String requestTSNameEN;              //tên yêu cầu tiếng anh
    private double amountTrans;         // tiền giao dịch tra soát
    private String ccyTrans;         // loai tiền giao dịch tra soát
    private String serviceCodeTrans;    //loại giao dịch
    private String serviceCodeTransName;// tên loại giao dịch
    private String reasonID;        //mã lí do
    private String reasonName;      //tên lí do
    private String reasonNameEN;    //tên lí do tiếng anh
    private double feeTSOL;         // tổng tiền phí cả vat VNĐ
    private double feeTSOLOrigin;   // tổng tiền phí cả vat
    private double feeTSOLVND;      // tổng tiền phí cả vat vnd
    private double feeTSOLAmtOrigin;    // tiền phí chưa vat
    private double feeTSOLAmtVND;       // tiền phí chưa vat vnd
    private double vatTSOLAmtOrigin;    // tiền vat
    private double vatTSOLAmtVND;       // tiền vat vnđ
    private Long idOld;             //id giao dịch được tra soát
    private String createdDateOld;  //ngày tạo giao dịch đc tra soát
    private String creditNameOld;   //Tên người hưởng giao dịch đc tra soát
    private String creditBankNameOld;   //tên ngân hàng hưởng giao dịch được tra soát
    private String creditAccOld;    //số tk người hưởng giao dịch được tra soát
    private String idNoOld;         // số giấy tờ tùy thân giao dịch được tra soát
    private String issueDateOld;    // ngày cấp số giấy tờ tùy thân  giao dịch được tra soát
    private String issuePlaceOld;   // nơi cấp số giấy tờ tùy thân giao dịch được tra soát
    private String remarkOld;
}
