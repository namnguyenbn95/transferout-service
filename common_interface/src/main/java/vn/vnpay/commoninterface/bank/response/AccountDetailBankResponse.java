package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDetailBankResponse extends BaseBankResponse {

    // Tên tài khoản của khách hàng
    // example: CT TEST DUONGDS
    private String accountName;

    // Địa chỉ tk khách hàng
    // example: abc abc 2abc 3abc 4
    private String accountAddress;

    // Tên rút gọn của tài khoản
    // example: CT TEST DUONGDS
    private String shortName;

    // CIF của khách hàng
    // example:20000368
    private String cif;

    // Loại tài khoản của khách hàng(D, S, T, L)
    // example:D
    private String accountType;

    // Số dư khả dụng
    // example:20420032
    private String avaiableAmount;

    // Số dư Collected balance
    // example:20420032
    private String collectedBalance;

    // Số dư gốc
    // example:20420032
    private String accountBal;

    // Previous statement balance
    // example:19112032
    private String lastDepAmt;

    // Số dư lần giao dịch gần nhất
    // example:970012
    private String stmtBal;

    // Số dư khoanh giữ
    private String holdAmt;

    // Accrued interest ACCRUE
    // example:457.5
    private String accruedInt;

    // Amt of last int Pd
    // example:4
    private String lastIntPaid;

    // YTD interest paid
    // example:438
    private String ytdInt;

    // Unused Credit Ln(ODLimit)
    private String odLimitAmt;

    // Interest Rate
    // example:0.2
    private String intRate;

    // Ngày giao dịch gần nhất
    private String lastActDate;

    // Ngày mở tài khoản
    private String openDate;

    // Date last deposit 7
    private String lastDepDate;

    // Date last statement 7
    private String lastStmtDate;

    // VarianceRate
    private String varianceRate;

    // FloorRate
    // example:0.002
    private String floorRate;

    // CeilingRate
    // example:0.006
    private String ceilingRate;

    // ODIntAmt
    private String odIntAmt;

    // Mã sản phẩm của tài khoản
    // example:10017
    private String productCode;

    // CreditAdviceFlag
    // example:10014
    private String creditAdviceFlag;

    // Currency của tk(VND...)
    // example:VND
    private String curCode;

    // Trạng thái của tài khoản
    // example:1
    private String accountStatus;

    // Mã chi nhánh của tài khoản
    // example:6800
    private String branchNo;

    // Số tài khoản core mới của khách hàng
    // example:1000000020
    private String accountNo;

    // Số tài khoản alias của khách hàng
    // example:0011000333211
    private String accountAlias;

    // Số dư khả dụng không bao gồm thấu chi
    // example:20420032
    private String acctBalWH;

    /**
     * FD Account
     */
    // IntPenalty
    private String intPenaltyAmt;

    // Số dư thực trả cho khách hàng NetToCustAmt
    // example: 11000000
    private String netToCustAmt;

    // Original Amount
    // example: 11000000
    private String orgBalAmount;

    // Time deposit term
    // example: 3
    private String depInfoTerm;

    // Receipt serial number (số sổ)
    // example: 0
    private String passbook;

    // Ngày đến hạn MatDate
    private String matDate;

    // Time deposit term code
    // example: M
    private String depInfoTermCode;

    // Interest Disposition
    // example: C
    private String dispInt;

    // Variable Rate Number
    private String rateNo;

    // Last Renewal Date
    private String lastRenDate;

    // LastPmtDate
    private String lastPmtDate;

    // NextIntDate
    private String nextIntDate;

    // MatDate having logic
    private String matDateHavingLogic;

    // RenOption
    // example: S
    private String renOption;

    // CollHoldAmt
    private String collHoldAmt;

    // OrgMatDate
    private String orgMatDate;

    /**
     * LN Account
     */
    // AANo
    private String aaNo;

    // FacilityCode
    // example: 1
    private String facilityCode;

    // FirstRelDate
    private String firstRelDate;

    // FullRelDate
    private String fullRelDate;

    // AccrualInt
    // example: 65206
    private String accrualInt;

    // NextMatDate
    private String nextMatDate;

    // Duration LoanTerm
    // example: 60
    private String loanTerm;

    // Duration LoanTermCode
    // example: M
    private String loanTermCode;

    // LoanType
    private String loanType;

    // Accrued penalty Charge
    private String penIntAmt;

    // Next Payment Amount
    // example: 378192
    private String nextPmtAmt;

    // NextPmtDueDate
    private String nextPmtDueDate;

    // NextIntPmtDueDate
    private String nextIntPmtDueDate;

    // LoanOrgDate
    private String loanOrgDate;

    // MsgID esb
    // example:871e8435-9c39-4a57-8f98-3271d6b01c28
    private String msgID;
}