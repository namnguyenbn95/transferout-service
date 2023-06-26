package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_ACCOUNT_FREE_TRANSFER")
public class MbAccountFreeTrans implements Serializable {

    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "CREDIT_ACCOUNT_CODE")
    private String creditAccountCode;

    @Column(name = "CREDIT_ACCOUNT_NAME")
    private String creditAccountName;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "FROM_DATE")
    private LocalDateTime fromDate;

    @Column(name = "TO_DATE")
    private LocalDateTime toDate;

    @Column(name = "STATUS")
    private String status;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "CREATED_USER")
    String createdUser;

    @Column(name = "UPDATED_USER")
    String updatedUser;
}
