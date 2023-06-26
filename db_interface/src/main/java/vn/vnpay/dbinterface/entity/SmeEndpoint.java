package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_ENDPOINT")
public class SmeEndpoint implements Serializable {

    private static final long serialVersionUID = -1538579436749954515L;

    @Id
    @Column(name = "ENDPOINT")
    private String endpoint;

    @Column(name = "DESCRIPTION")
    private String desc;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "APP_NAME")
    private String appName;

    @Column(name = "IS_FINANCE")
    private String isFinance;

    @Column(name = "IS_PUBLIC")
    private String isPublic;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "CAPTCHA_IB")
    private String captchaIb;
}
