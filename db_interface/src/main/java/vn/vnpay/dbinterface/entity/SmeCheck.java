package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_CHECK")
public class SmeCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_CHECK")
    @SequenceGenerator(name = "idSeq_SME_CHECK", sequenceName = "SME_CHECK_SEQ", allocationSize = 1)
    @Column(name = "CHECK_ID")
    private long checkId;

    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "CHECK_TYPE")
    private String checkType;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "COUNT")
    private Integer count;

    @Column(name = "IMEI")
    private String imei;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
