package vn.vnpay.dbinterface.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SME_CUSTOMERS_USERS_ACTIONHIS")
public class SmeCustomerUserActionHis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_CUSTOMERS_USERS_ACTIONHIS")
    @SequenceGenerator(name = "idSeq_SME_CUSTOMERS_USERS_ACTIONHIS", sequenceName = "SME_CUSTOMERS_USERS_ACTIONHIS_SEQ", allocationSize = 1)
    @Column(name = "USER_ACTIONHIS_ID")
    private long id;

    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "CIF_CORE")
    private String cif;

    @Column(name = "CUS_NAME")
    private String cusName;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "POS_CODE")
    private String posCode;

    @Column(name = "CUS_USER_ID")
    private Long cusUserId;

    @Column(name = "MOBILE_OTP")
    private String mobileOtp;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ACTION_TYPE")
    private String actionType;

    @Column(name = "ACTION_USER")
    private String actionUser;

    @Column(name = "ACTION_DATE")
    private LocalDateTime actionDate;

    @Column(name = "CONFIRM_USER")
    private String confirmUser;

    @Column(name = "CONFIRM_DATE")
    private LocalDateTime confirmDate;

    @Column(name = "CONFIRM_TYPE")
    private String confirmType;

    @Column(name = "INFOR_UPDATE_DESC")
    private String infoUpdateDesc;
}
