package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class FeeReportEntity {
    @Id
    @Column(name = "TRANX_ID")
    private long tranxId;

    @Column(name = "FEE")
    private double fee;

    @Column(name = "VAT")
    private double vat;

    @Column(name = "FEEVAT")
    private double feeVat;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "TRANX_TIME")
    private LocalDateTime tranxTime;

    @Column(name = "FROM_ACC")
    private String fromAcc;

    @Column(name = "FEE_TYPE")
    private String feeType;

    @Column(name = "TOTAL_AMOUNT")
    private double totalAmount;

    @Column(name = "CCY")
    private String ccy;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "tranx_type")
    private String tranxType;
}
