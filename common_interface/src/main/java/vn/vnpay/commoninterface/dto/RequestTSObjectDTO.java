package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestTSObjectDTO {
    private double requestTSID;
    private String requestTSName;
    private String requestTSName_EN;
    private List<ReasonObjectDTO> listReasonObject;
}
