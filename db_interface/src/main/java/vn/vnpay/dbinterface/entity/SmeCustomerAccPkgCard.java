package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SME_CUSTOMERS_ACC_PKG_CARD")
public class SmeCustomerAccPkgCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "CRD_NBR")
    private String crdNbr;

    @Column(name = "IS_DEBIT")
    private String isDebit;

    @Column(name = "ACC_PKG_CODE")
    private String accPkgCode;

    @Column(name = "IS_CURRENT")
    private String isCurrent;
}

