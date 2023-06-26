package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseContentAcc247Inquiry {
    private String code;
    private String name;
    private int route;
    private String routeName;
    private String accountPosting;
    private String accountPostingType;
}
