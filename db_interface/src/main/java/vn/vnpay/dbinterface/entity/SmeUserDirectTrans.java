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
@Table(name = "SME_USER_DIRECT_TRANS")
public class SmeUserDirectTrans {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_USER_DIRECT_TRANS")
    @SequenceGenerator(name = "idSeq_SME_USER_DIRECT_TRANS", sequenceName = "SME_USER_DIRECT_TRANS_SEQ", allocationSize = 1)
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

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}
