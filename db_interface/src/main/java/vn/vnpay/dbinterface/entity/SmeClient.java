package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_CLIENTS")
public class SmeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_CLIENTS")
    @SequenceGenerator(name = "idSeq_SME_CLIENTS", sequenceName = "SME_CLIENTS_SEQ", allocationSize = 1)
    @Column(name = "CLIENT_ID")
    private long clientId;

    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "KEY_ID")
    private long keyId;

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

    @Column(name = "TOKEN_LOGIN")
    private String tokenLogin;

    @Column(name = "TOKEN_TRANS")
    private String tokenTrans;
}
