package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransLogsDTO {
    private String transDate;
    //Transaction  date dd/MM/yyyy HH:mm:ss
    private String procStatus;
    //Process status of transaction POSTOK - Thành công POSTFAIL - Thất bại POSTEX - Chưa xác định
    private String responseCode;
    //0  - Mã lỗi hiển th
    private String responseMessage;
    // Mô tả lỗi hiển thị
}
