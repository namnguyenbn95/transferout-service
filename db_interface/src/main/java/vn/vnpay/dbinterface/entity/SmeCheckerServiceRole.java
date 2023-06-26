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
@Table(name = "SME_CHECKER_SERVICE_ROLE")
public class SmeCheckerServiceRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_CHECKER_SERVICE_ROLE")
    @SequenceGenerator(name = "idSeq_SME_CHECKER_SERVICE_ROLE", sequenceName = "SME_CHECKER_SERVICE_ROLE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private long id;

    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "ADMIN_USER")
    private String adminUser;

    @Column(name = "IS_TRANS")
    private String isTrans;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CUS_USERNAME")
    private String cusUsername;

    @Column(name = "MAKER_USER")
    private String makerUser;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}
