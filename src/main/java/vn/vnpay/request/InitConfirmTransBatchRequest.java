package vn.vnpay.request;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.request.BaseClientRequest;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class InitConfirmTransBatchRequest extends BaseClientRequest {
    @NotEmpty
    private List<String> tranxIds;
    private String reason;
}
