package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.CashFlowItemDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class GetCashFlowListBankReq extends BaseBankRequest{
    private String accountNoNew;
    private String accountType;
    private List<CashFlowItemDTO> listCashFlowIn;
    //Loại tìm kiếm (0 = theo ngày, 1 = theo tuần, 2 = theo tháng
    private int typeRequest;
}

