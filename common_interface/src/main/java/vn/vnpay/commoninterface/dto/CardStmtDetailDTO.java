package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardStmtDetailDTO {
    private String creditLimit;         // Hạn mức tín dụng
    private String dueDate;             // Ngày phải thanh toán (2020-09-20)
    private String amount;              // Số dư
    private String dueAmount;           // Số tiền thanh toán tối thiểu
    private String preAmount;           // Số dư kỳ trước
}