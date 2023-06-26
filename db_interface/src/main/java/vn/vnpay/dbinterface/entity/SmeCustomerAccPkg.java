package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SME_CUSTOMERS_ACC_PKG")
public class SmeCustomerAccPkg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "ACC_PKG_CODE")
    private String pkgCode;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @CreatedDate
    @Column(name = "REGIST_DATE", nullable = false, updatable = false)
    private LocalDateTime registDate;

    @Column(name = "REGIST_CHANNEL")
    private String registChannel;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_BY")
    private String createdBy;
}
