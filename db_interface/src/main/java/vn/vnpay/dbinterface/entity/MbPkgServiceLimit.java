package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_PACKAGE_SERVICE_LIMITS")
public class MbPkgServiceLimit {

    @Id
    @Column(name = "PACKAGE_SERVICE_LIMIT_ID")
    private long id;

    @Column(name = "PACKAGE_CODE")
    private String packageCode;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "METHOD_OTP")
    private String methodOtp;

    @Column(name = "CURRENCY")
    private String curCode;

    @Column(name = "TIME_LIMIT")
    private long timeLimit;

    @Column(name = "MIN_LIMIT")
    private long minLimit;

    @Column(name = "MAX_LIMIT")
    private long maxLimit;

    @Column(name = "TOTAL_LIMIT")
    private long totalLimit;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_USER")
    private String createdUser;

    @Column(name = "UPDATED_USER")
    private String updatedUser;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

}
