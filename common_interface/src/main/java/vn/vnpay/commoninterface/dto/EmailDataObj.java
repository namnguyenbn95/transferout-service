package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.dbinterface.dto.DebitCardDTO;
import vn.vnpay.dbinterface.entity.SmeTrans;
import vn.vnpay.dbinterface.entity.pcm.AddlFieldMetaData;
import vn.vnpay.dbinterface.entity.pcm.BillRec;
import vn.vnpay.dbinterface.entity.pcm.SvcIdent;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@Builder
public class EmailDataObj {
    private String beneBankName;
    private String beneCityCode;
    private String beneCityName;
    private String beneBranchName;

    private DebitAccountDTO debitAccount;
    private CreditAccountDTO creditAccount;
    private DebitCardDTO debitCard;
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

    // Check credit VRA
    private boolean isCreditVRA;
    private String vaName;
    private String vaStatus;
    private String vrAccount;
    private String realAccountName;
    private String realAccountNumber;

    // route for napas
    Integer adviceRoute;

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

    String remark;
    String creditAdviceFlag;

    // Tax data
    private TaxPaymentInfoDTO taxPaymentInfo;
    private RegistrationTaxDTO registrationTax;

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

    // email data
    String tranxTime;
    String debitAmount;
    String debitAmountOrg;
    String creditAmount;
    String creditAmountOrg;
    String feeType;
    String feeTypeEn;
    String tranxId;
    String feeS;
    String vatS;
    String totalFeeS;

    public void formatStr() {
        try {
            tranxTime = CommonUtils.TimeUtils.format("dd/MM/yyyy HH:mm:ss"
                    , Date.from(orgTran.getTranxTime().atZone(ZoneId.systemDefault()).toInstant()));

            debitAmount = CommonUtils.formatAmount(debitAccount.getAmountVND(), "VND");
            debitAmountOrg = CommonUtils.formatAmount(debitAccount.getOriginAmount(), debitAccount.getCurrency());
            creditAmount = CommonUtils.formatAmount(creditAccount.getAmountVND(), "VND");
            creditAmountOrg = CommonUtils.formatAmount(creditAccount.getOriginAmount(), creditAccount.getCurrency());

            feeS = CommonUtils.formatAmount(fee.getOriginAmount(), fee.getCurrency());
            vatS = CommonUtils.formatAmount(fee.getOriginVatAmount(), fee.getCurrency());
            totalFeeS = CommonUtils.formatAmount(fee.getOriginAmount() + fee.getOriginVatAmount(), fee.getCurrency());

            tranxId = String.valueOf(orgTran.getId());

            feeType = "1".equals(orgTran.getFeeType()) ?
                    "Phí trong" : "Phí ngoài";
            feeTypeEn = "1".equals(orgTran.getFeeType()) ?
                    "Internal" : "External";
        } catch (Exception e) {

        }
    }
}
