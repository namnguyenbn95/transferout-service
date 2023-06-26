package vn.vnpay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.RelationShipObjectDTO;
import vn.vnpay.commoninterface.dto.RequestTSObjectDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class GetDetailTransChargebackResponse {
    private String serviceCode;
    private String serviceName;
    private String serviceNameEn;
    private String channel;
    private String createdDate;
    private double amount;
    private String creditAcc;
    private String creditName;
    private String bankCode;
    private String bankName;
    private String content;
    private String idNo;
    private String issueDate;
    private String issuePlace;
    private String status;
    private String serialNo;
    private String currency;
    private List<RequestTSObjectDTO> listRequestTSObject;
    private String teller;
    private int sequence;
    private String hostdate;
    private String pcTime;
}
