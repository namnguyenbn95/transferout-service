package vn.vnpay.commoninterface.bank.response;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.commoninterface.dto.HardTokenUsersDTO;

import java.util.List;

@Getter
@Setter
public class HardTokenInquiryResponse extends BaseBankResponse {
    private List<HardTokenUsersDTO> content;
}
