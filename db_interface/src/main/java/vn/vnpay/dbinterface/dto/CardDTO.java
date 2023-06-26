package vn.vnpay.dbinterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDTO {
    // Số CIF trên DB thẻ
    private String custName;

    // Tài khoản thẻ
    private String acctNbr;

    // Số thẻ đã bị mask theo chuẩn PCI
    private String crdNbr;

    // Mã khóa thẻ, nếu có giá trị dưới đây thì thẻ đang khóa (A, L, R, S, W, X, Y, Z)
    private String crdBlk;
    private String lastCrdBlk;

    private String custAddr1;
    private String custAddr2;
    private String custCity;
    private String custPhone;

    // Số CIF trên DB thẻ
    private String custNbr;

    // bankNumber
    private String custBank;

    // Cust Class Type
    private String custType;

    // Trạng thái của thẻ, nếu có giá trị dưới đây thì thẻ đang active. "1", "2"
    private String custStat;

    // Corp Cif
    private String corpCif;

    // Ngày mở thẻ
    private String crddtOpen;

    // Ngày khóa thẻ
    private String crdBlkDte;

    // Mã sản phẩn thẻ
    private String crdPdtNbr;

    // Ngày hết hạn thẻ
    private String crdExpDte;

    // Usage Limit Code
    private String crdAutUsg;

    // Trạng thái của account, nếu có giá trị dưới đây thì thẻ đang active. "1", "2"
    private String acctStat;
    private double outStdBalAmount;
    private double creditAvailAmount;
    private double creditLimitAmount;
    private double cashAvailAmount;
    private double cashLimitAmount;
    private double retailBalAmount;
    private double cashBalanceAmount;
    private String productDesc;
    private String supRel;
    private long rowNum;

    // True: thẻ debit. False: thẻ credit
    private boolean isDebit;

    // Tên thẻ (AMEX, JCB, …)
    private String cardName;

    // url phôi thẻ
    private String cardFormUrl;
    private String cardSize;
    private String pdtName;

    //message status thẻ
    private String msgStt;
}
