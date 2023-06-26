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
@Table(name = "MB_SERVICE_DISPLAY_GROUP")
public class MbServiceDisplayGroup {
    @Id
    @Column(name = "DISPLAY_GROUP_ID")
    private Long id;

    @Column(name = "DISPLAY_GROUP_NAME")
    private String name;

    @Column(name = "DISPLAY_GROUP_NAME_EN")
    private String nameEng;

    @Column(name = "DISPLAY_LEVEL")
    private String level;

    @Column(name = "SERVICE_CODES")
    private String serviceCodes;

    @Column(name = "BILL_SERVICE_CODE")
    private String billServiceCode;

    @Column(name = "DISPLAY_POS")
    private Integer displayPos;

    @Column(name = "SERVICETYPE_CODE")
    private String serviceTypeCode;
}
