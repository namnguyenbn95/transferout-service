package vn.vnpay.commoninterface.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountListTransManageRequest extends BaseClientRequest {
    private List<String> listServiceCode;
    private boolean isReport;
}
