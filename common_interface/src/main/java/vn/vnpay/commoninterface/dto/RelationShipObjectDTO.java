package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RelationShipObjectDTO {
    private List<RequestTSObjectDTO> listRequestTSObject;
}
