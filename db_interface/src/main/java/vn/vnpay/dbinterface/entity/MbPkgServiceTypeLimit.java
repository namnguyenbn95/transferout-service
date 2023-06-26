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
@Table(name = "mb_package_service_type_limits")
public class MbPkgServiceTypeLimit {

    @Id
    @Column(name = "package_service_type_limit_id")
    private Long id;

    @Column(name = "PACKAGE_CODE")
    private String packageCode;

    @Column(name = "servicetype_code")
    private String servicetypeCode;

    @Column(name = "method_otp")
    private String methodOtp;

    @Column(name = "currency")
    private String currency;

    @Column(name = "total_limit")
    private Long totalLimit;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_user")
    private String createdUser;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_user")
    private String updatedUser;

    @Column(name = "default_limit")
    private Long defaultLimit;

}
