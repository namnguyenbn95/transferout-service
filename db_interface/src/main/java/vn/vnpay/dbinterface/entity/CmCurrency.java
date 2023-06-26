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
@Table(name = "CM_CURRENCY")
public class CmCurrency {
    @Id
    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    @Column(name = "CURRENCY_NAME")
    private String currencyName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_DECIMAL")
    private String isDecimal;
}
