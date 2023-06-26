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
@Table(name = "AD_BIN_CARD")
public class BinCard implements Serializable {

    @Id
    @Column(name = "BIN_CARD_NAME")
    private String binCode;

    @Column(name = "BIN_CARD_VALUE")
    private String binValue;

    @Column(name = "STATUS")
    private String status;
}
