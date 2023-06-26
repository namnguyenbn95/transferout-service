package vn.vnpay.commoninterface.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardWaitingDTO {
    // Ngày cấp phép
    private String waitingDate;

    // Số tiền cấp phép
    private String issueAmount;

    // Số cấp phép
    private String issueNumber;

    // Ngày còn lại
    private String remainDate;

    // Ghi chú
    private String remark;

    // Số tham chiếu
    private String refNumber;
}
