package vn.vnpay.commoninterface.response.softotp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckStatusRp {
    private String code;
    private String message;
    private String userName;
    private String DEVICE_TYPE;
    private String GCM_ID;
    private String USER_ID;
    private String STATUS;
    private String CHANNEL;
    private String TYPESTATUS;
    private String OLDSTATUS;
    private String dateUnlock;
    private String dateUnlockFormat;
}
