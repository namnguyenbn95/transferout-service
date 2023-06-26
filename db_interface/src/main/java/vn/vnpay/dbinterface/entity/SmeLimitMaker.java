package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "sme_limit_maker")
public class SmeLimitMaker {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idsme_limit_maker_seq")
    @SequenceGenerator(name = "idsme_limit_maker_seq", sequenceName = "sme_limit_maker_seq", allocationSize = 1)
    @Column(name = "limit_maker_id")
    private long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_created")
    private String userCreated;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "ccy")
    private String ccy;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "method_otp")
    private String methodOtp;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Transient
    private Long amountVND;

    @Transient
    private Long amountUSD;

}
