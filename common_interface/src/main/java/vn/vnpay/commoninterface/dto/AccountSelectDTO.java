package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AccountSelectDTO {
    private String binNo;
    private String accountNo;
    private String binType;
    private String price;
    private BigDecimal promoteValue;
    private String binTypeName;
    private String binTypeNameEn;
    private String description;
    private String descriptionEn;
    private BigDecimal vat;
    private BigDecimal priceLast;
    private String accBranchName;
    private String accBranchAddr;
    private BigDecimal pricePrefer;
    private String binNumberChar;
    private String branchCode;
    private String updateFee;
    private BigDecimal priceDecimal;
    private Boolean isShowPricePrefer;
}
