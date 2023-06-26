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
@Table(name = "SME_CLIENTS_HIS")
public class SmeClientHis {

    @Id
    @Column(name = "CLIENT_ID")
    private Long clientId;

    @Column(name = "CUS_USER_ID")
    private Long cusUserId;

    @Column(name = "KEY_ID")
    private Long keyId;

    @Column(name = "IP_REQUEST")
    private String ipRequest;

    @Column(name = "OS")
    private String os;

    @Column(name = "IMEI")
    private String imei;

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "MOBILE_STATUS")
    private String mobileStatus = "1";

    @Column(name = "PM")
    private String pm;

    @Column(name = "OV")
    private String ov;

    @Column(name = "PS")
    private String ps;

    @Column(name = "CANCELED_DATE")
    private LocalDateTime canceledDate;

    @Column(name = "TOKEN_LOGIN")
    private String tokenLogin;

    @Column(name = "TOKEN_TRANS")
    private String tokenTrans;
}
