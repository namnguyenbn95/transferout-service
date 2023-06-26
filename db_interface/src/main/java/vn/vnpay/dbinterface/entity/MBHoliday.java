package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "MB_HOLIDAY")
public class MBHoliday {
    @Id
    @Column(name = "HOLIDAY_ID")
    private int holidayId;

    @Column(name = "HOLIDAY_TYPE")
    private String holidayType;

    @Column(name = "DAY_OFF")
    private String dayOff;

    @Column(name = "STATUS")
    private String status;
}
