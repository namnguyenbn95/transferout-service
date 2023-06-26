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
@Table(name = "SME_CUSTOMERS_ACTION_ACC_PKG")
public class SmeCustomerAccPkgAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTION_ID")
    private long id;

    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "CUS_NAME")
    private String cusName;

    @Column(name = "ACTION_TYPE")
    private String type;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "IP_IMEI")
    private String imei;

    @Column(name = "ACTION_USER")
    private String user;

    @CreatedDate
    @Column(name = "ACTION_DATE", nullable = false, updatable = false)
    private LocalDateTime actionDate;

    @Column(name = "CONFIRM_USER")
    private String confirmUser;

    @Column(name = "CONFIRM_DATE")
    private LocalDateTime confirmDate;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @Column(name = "METADATA")
    private String metadata;

    @Column(name = "ACTION_STATUS")
    private String status;

    @Column(name = "CIF_INT")
    private int cifInt;
}
