package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Cities {
    @Id
    @Column(name = "CITY_CODE")
    private String cityCode;

    @Column(name = "CITY_NAME")
    private String cityName;
}
