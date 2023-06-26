package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EwalletCustomerDTO {
    private String customerCode;
    private String customerBankAccount;
    private String customerName;

}
