package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.pcm.AddlFieldMetaData;
import vn.vnpay.dbinterface.entity.pcm.BillField;
import vn.vnpay.dbinterface.entity.pcm.BillRec;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
public class TransactionDTO {
    private long id;
    private String tranxType;
    private String createdUser;
    private String createdMobile;
    private String checkerAuthenType;
    private String approvedUser;
    private String approvedDate;
    private String approvedMobile;
    private String fromAcc;
    private String toAcc;
    private String providerCode;
    private String serviceCode;

    private String amount;

    private String tranxNote;
    private String status;
    private String tranxTime;
    private String resBankCode;
    private String resBankDesc;

    private String flatFee;
    private String feeOnAmt;
    private String totalAmount;

    private String ccy;
    private String tranxRemark;
    private String productType;
    private String tranxRefno;
    private String beneBranchCode;
    private String branchCode;
    private String serviceType;
    private String feeType;
    private String creditName;
    private String beneBankCode;
    private String cusName;
    private String cifNo;
    private String makerAuthenType;
    private String tranxContent;
    private String metadata;
    private String channel;
    private String reason;


    private String amountVND;
    private String originAmount;
    private String totalFee;
    private String exchangeTotalFee;
    private String totalFeeU;

    private CronJobDTO cronJob;
    private String beneBankName;
    private String beneCityName;
    private String beneCityCode;
    private String beneBranchName;
    private String vcbToken;
    private LocalDateTime tranxTimeLocal;

    // billing
    private String billServiceCode;
    private String billServiceName;
    private String billServiceNameEn;
    private String billProviderCode;
    private String billProviderName;
    private String billProviderNameEn;
    private String billSubProviderCode;
    private String billSubProviderName;
    private String billSubProviderNameEn;
    private String cusRefCode;
    private String addInfo;
    ArrayList<BillRec> billRec;
    ArrayList<AddlFieldMetaData> addlFieldMetaData;
    String labelTextVn;
    String labelTextEn;
    ArrayList<BillField> billFields;
    String companyCode;
    private TaxPaymentInfoDTO taxPaymentInfo;
    private CustomsTaxInfoDTO customsTaxInfo;
    private RegistrationTaxDTO registrationTax;
    private BHXHDataDTO bhxhData;
    private GetSeaPortPaymentInfoDTO seaportPaymentInfo;
    private GetSeaPortPaymentInfoHCMDTO seaportPaymentInfoHCM;

    private int orderNumber;
    private String unit;
    private String paymentType;
    private String crdNbr;
    private String recordDate;
    private String chargeType;
    private String fullname;
    private String idType;
    private String idTypeText;
    private String idNo;
    private String issueDate;
    private String issuePlace;
    private String idIssuedPlace;
    private boolean isTranViaCard;
    private String batchId;
    private String refNo;

    //add loan payment
    private String facilityNo;      //số hợp đồng
    private Double prepayPenalty;   //phí trả nợ trước hạn
    private Double prepayPenaltyOrigin;

    //add tra soát
    private String statusChargeback; //trạng thái giao dịch tra soát
    private String tsoLRef;         //mã yêu cầu tra soát
    private String requestTSID;     //loại yêu cầu tra soát
    private String requestTSName;                 //tên yêu cầu
    private String requestTSNameEN;              //tên yêu cầu tiếng anh
    private double amountTrans;     //số tiền giao dịch gốc
    private String ccyTrans;        //ccy giao dịch gốc
    private String serviceCodeTrans;    //loại giao dịch
    private String serviceCodeTransName;// tên loại giao dịch
    private String reasonID;        //mã lí do
    private String reasonName;      //tên lí do
    private String creditAccChange;     //số tk hưởng thay đổi
    private String creaditNameChange;   //tên tk hưởng thay đổi
    private String contentChange;       //nội dung thay đổi
    private String idNoChange;          //số gttt thay đổi
    private String issueDateChange;     //ngày cấp gttt thay đổi
    private String issuePlaceChange;    //nơi cấp gttt thay đổi
    private String reasonNameEN;    //tên lí do tiếng anh
    private double feeTSOL;         //tiền phí
    private String createdDateOld;  //ngày tạo giao dịch đc tra soát
    private String creditNameOld;   //Tên người hưởng giao dịch đc tra soát
    private String creditBankNameOld;   //tên ngân hàng hưởng giao dịch được tra soát
    private String creditAccOld;    //số tk người hưởng giao dịch được tra soát
    private String idNoOld;         // số giấy tờ tùy thân giao dịch được tra soát
    private String issueDateOld;    // ngày cấp số giấy tờ tùy thân  giao dịch được tra soát
    private String issuePlaceOld;   // nơi cấp số giấy tờ tùy thân giao dịch được tra soát
}
