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
@Table(name = "MB_TAX_TYPES")
public class TaxType {
    @Id
    @Column(name = "TAX_TYPE_CODE")
    private String typeCode;

    @Column(name = "TAX_TYPE_NAME")
    private String typeName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "TAX_TYPE_SHORT_NAME")
    private String shortName;
}
