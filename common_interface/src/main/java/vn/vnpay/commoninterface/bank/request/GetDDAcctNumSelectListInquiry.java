package vn.vnpay.commoninterface.bank.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetDDAcctNumSelectListInquiry extends BaseBankRequest{

    //Số lượng chữ số đuôi
    private String binNumber;

    //Loại số chọn 1 - Lộc phát, 2 - Thần tài,
    private String binType;

    //example: 9
    private String prefixNumber;
}

