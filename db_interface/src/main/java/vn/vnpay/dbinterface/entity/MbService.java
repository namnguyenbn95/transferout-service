package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "MB_SERVICE")
public class MbService {

    @Id
    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "SERVICE_NAME_EN")
    private String serviceNameEn;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SERVICE_DESC")
    private String serviceDesc;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "TELLER_ID")
    private String tellerId;

    @Column(name = "IS_ALLOW_OVERDRAFT")
    private String isAllowOverDraft;

    @Column(name = "IS_FINANCIAL")
    private String isFinancial;

    @Column(name = "IS_TRANS")
    private String isTrans;

    @Column(name = "IS_DIRECT")
    private String isDirect;

    @Transient
    private String billServiceCode;
    @Transient
    private String billServiceName;
    @Transient
    private String serviceGroupName;
    @Transient
    private String type;
}
