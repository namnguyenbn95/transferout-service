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
@Table(name = "MB_ACCOUNT_FREE_TRANSFER_SERVICE")
public class MbAccountFreeTransServ implements Serializable {
    @Id
    @Column(name = "ACCOUNT_FREE_TRANSFER_SERVICE_ID")
    private long id;

    @Column(name = "ACCOUNT_FREE_TRANSFER_ID")
    private Long accFreeTransId;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;
}
