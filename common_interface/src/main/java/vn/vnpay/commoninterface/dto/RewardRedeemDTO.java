package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardRedeemDTO {
    private String rW_TXN_DETAIL_ID;
    private String rW_TXN_CHANNEL_ID;
    private String rW_GIFTTYPE_CODE;
    private String rW_CREATED_DATE;
    private String rW_MASKING_CARD_NUMBER;
    private double rW_TOTAL_AMOUNT;
    private double rW_TOTAL_POINT;
    private String rW_CHARITY_GROUP;
    private String rW_DONATE_NAME;
    private String rW_DONATE_ADDR;
    private String rW_DONATE_CONTENT;
    private String rW_CHANNEL_NAME;
    private String rW_AMOUNT_GIFT;
    private String rW_POVIDER_NAME;
    private String rW_PHONE_NUM;
    private String rW_ORDER_ID;
    private String rW_ACCOUNT_ROOT;
    private double rW_AMOUNT_ROOT;
    private String rW_LICENSE_CUS;
    private double rW_FEE_AMOUNT;
    private String rW_TERM_TV;
}
