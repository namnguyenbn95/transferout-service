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
@Table(name = "MB_INSURANCES")
public class MbInsurance {
    @Id
    @Column(name = "INSURANCE_ID")
    private long id;

    @Column(name = "INSURANCE_PAYER")
    private String insurancePayer;

    @Column(name = "INSURANCE_CODE")
    private String insuranceCode;

    @Column(name = "INSURANCE_NAME")
    private String insurancename;

    @Column(name = "INSURANCE_NAME_EN")
    private String insurancenameEn;

    @Column(name = "STATUS")
    private String status;
}
