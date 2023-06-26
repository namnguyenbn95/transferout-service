package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "MB_CONFIG")
public class MbConfig implements Serializable {

    private static final long serialVersionUID = 6911500198477826014L;

    @Id
    @Column(name = "CONFIG_CODE")
    private String code;

    @Column(name = "CONFIG_VALUE")
    private String value;

    @Column(name = "CONFIG_DESC")
    private String desc;

    @Column(name = "CONFIG_STATUS")
    private String status;
}
