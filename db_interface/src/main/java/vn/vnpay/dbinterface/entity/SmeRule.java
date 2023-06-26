package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_RULE")
public class SmeRule {
    @Id
    @Column(name = "RULE_ID")
    private long id;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "DEBIT_ACCOUNT")
    private String debitAccount;

    @Column(name = "CREDIT_ACCOUNT")
    private String creditAccount;

    @Column(name = "DEBIT_ACCOUNT_EXTEND")
    private String debitAccountExt;

    @Column(name = "CREDIT_ACCOUNT_EXTEND")
    private String creditAccountExt;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_USER")
    private String createdUser;

    @Column(name = "UPDATED_USER")
    private String updatedUser;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}
