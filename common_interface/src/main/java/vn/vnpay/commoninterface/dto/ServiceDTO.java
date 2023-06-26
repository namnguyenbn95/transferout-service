package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDTO {
    private String serviceCode;
    private String serviceName;
    private String serviceTypeCode;
    private String serviceTypeName;
    private String isLike;
    private String isSuggest;
    private String isLastUsed;
    private String type; //1. Gợi ý, 2 gần đây, 3 yêu thích

    private String serviceGroup;
    private String serviceGroupName;
    private String billServiceCode;
    private String billServiceName;
    private int displayHomeId;

}
