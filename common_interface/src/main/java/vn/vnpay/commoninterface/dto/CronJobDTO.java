package vn.vnpay.commoninterface.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CronJobDTO {
    // 2021-05-31: Ngày hiệu lực - lệnh ngày tương lai
    private String specificDate;

    // Loại lặp - lệnh định kỳ Giá trị trong khoản 1 - 9
    private int interval;

    // Đơn vị lặp - lệnh định kỳ Ngày - 4 Tuần - 5 Tháng - 6
    private int unit;

    // 2021-05-17
    private String startDate;

    // 2021-05-31
    private String endDate;
}
