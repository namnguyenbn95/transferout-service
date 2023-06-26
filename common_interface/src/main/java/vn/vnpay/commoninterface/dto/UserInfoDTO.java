package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoDTO {

    private long cusUserId;
    private long cusId;
    private String username;
    private String pinType;
    private String cusStatus;
    private String cusUserStatus;
    private String mobileOtp;
    private String email;
    private String cif;
    private String lastLoginSource;
    private LocalDateTime autoUnlockTime;
    private LocalDateTime activedDate;
    private LocalDateTime pinExpireDate;
    private LocalDateTime lastLoginDate;
    private String roleType;
    private String isNotify;
    private String registCodeSmartOtp;
    private LocalDateTime registCodeSmartOtpExpire;

}
