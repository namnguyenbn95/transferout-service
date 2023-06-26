package vn.vnpay.dbinterface.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "CM_CITY")
public class CmCity {
    @Id
    @Column(name = "CITY_CODE")
    private String cityCode;

    @Column(name = "CITY_NAME")
    private String cityName;

    @Column(name = "STATUS")
    private String status;

}
