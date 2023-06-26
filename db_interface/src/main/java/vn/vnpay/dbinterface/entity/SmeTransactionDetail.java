package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "sme_transaction_detail")
public class SmeTransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idsme_trans_detail_seq")
    @SequenceGenerator(name = "idsme_trans_detail_seq", sequenceName = "sme_trans_detail_seq", allocationSize = 1)
    @Column(name = "TRANX_DETAIL_ID")
    private Long id;

    @Column(name = "TRANX_ID")
    private Long tranxId;

    @Column(name = "TRANX_PHARSE")
    private Integer tranxPhase;

    @Column(name = "RES_CODE")
    private String resCode;

    @Column(name = "RES_DES")
    private String resDesc;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "TRANX_NOTE")
    private String tranxNote;

    @Column(name = "DETAIL")
    private String detail;

    @Column(name = "SOURCE")
    private String source;

}
