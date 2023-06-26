package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bene247AccOutPostedResp {
    private String branchPosting;
    private String hostDatePosting;
    private String pcTimePosting;
    private Bene247AccOutPostingCoreResp postingCoreResponse;
    private String description;
    private Bene247AccOutPostingCoreReq postingRequest;
    private String sequencePosting;
    private String tellerPosting;
}
