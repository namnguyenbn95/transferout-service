package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class AuthenOTTEntity {
    @Id
    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "MOBILE_OTP")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "AUTHEN_NOTIFY")
    private String authenNotify;
}
