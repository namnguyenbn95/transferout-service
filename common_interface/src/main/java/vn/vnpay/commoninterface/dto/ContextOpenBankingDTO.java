package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContextOpenBankingDTO {
    private String appId;
    private String messageId;
    private String messageTime;
    private RoutingOpenBankingDTO routing;
}
