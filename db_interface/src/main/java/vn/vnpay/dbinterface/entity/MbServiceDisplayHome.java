package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "MB_SERVICE_DISPLAY_HOME")
public class MbServiceDisplayHome {
    @Id
    @Column(name = "DISPLAY_HOME_ID")
    private Long id;

    @Column(name = "SERVICETYPE_CODE")
    private String serviceTypeCode;

    @Column(name = "DISPLAY_LEVEL")
    private String displayLevel;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DISPLAY_NAME_EN")
    private String displayNameEn;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "OBJECT")
    private String object;

    @Column(name = "SERVICE_CODES")
    private String serviceCodes;

    @Column(name = "BILL_SERVICE_CODE")
    private String billServiceCode;

    @Column(name = "APPROVE_MODEL")
    private String approveModel;
}
