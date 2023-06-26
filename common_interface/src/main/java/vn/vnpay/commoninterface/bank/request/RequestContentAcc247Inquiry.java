package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestContentAcc247Inquiry {
    private String dbAccount;
    private String dbName;
    private String dbAddress;
    private String dbCard;
    private String account;
    private String bankInfo;
    private boolean card;
    private boolean token;
    private String systrace;
    private String termId;
    private String inquiryType;
    private String channel;
    private String bankType;
    private String currency;
}
