package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SME_USER_DIRECT_ECOM")
public class SmeUserDirectEcom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_USER_DIRECT_ECOM")
    @SequenceGenerator(name = "idSeq_SME_USER_DIRECT_ECOM", sequenceName = "SME_USER_DIRECT_ECOM_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private long id;

    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "ACC_NO")
    private String accNo;

    @Column(name = "ACC_ALIAS")
    private String accAlias;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "ADMIN_USER")
    private String adminUser;

    @Column(name = "CUR_CODE")
    private String curCode;

    @Column(name = "CUS_USERNAME")
    private String cusUsername;

    @Column(name = "MID")
    private String mid;

    @Column(name = "MID_NAME")
    private String midName;

    @Column(name = "TID")
    private String tid;

    @Column(name = "TID_NAME")
    private String tidName;

    @Column(name = "MIN_AMOUNT")
    private Double minAmount;

    @Column(name = "MAX_AMOUNT")
    private Double maxAmount;

    @Column(name = "MAX_AMOUNT_DAY")
    private Double maxAmountDay;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}
