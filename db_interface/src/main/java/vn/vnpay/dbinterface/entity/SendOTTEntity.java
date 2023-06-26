package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class SendOTTEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "MOBILE_OTP")
    private String phone;

    @Column(name = "IS_VIEW")
    private String isView;

    @Column(name = "ACC_TYPE")
    private String accType;
}
