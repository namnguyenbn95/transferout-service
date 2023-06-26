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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_PACKAGES")
public class MbPkgs implements Serializable {

    @Id
    @Column(name = "PACKAGE_CODE")
    private String pkgCode;

    @Column(name = "PACKAGE_NAME")
    private String pkgName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MONTHLY_FEE")
    private BigDecimal monthFee;

    @Column(name = "VAT")
    Long vat;

    @Column(name = "SERVICE_CHANNEL")
    private String servChannel;

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
