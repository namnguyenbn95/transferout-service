package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "SME_OTP")
public class SmeOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_OTP")
    @SequenceGenerator(name = "idSeq_SME_OTP", sequenceName = "SME_OTP_SEQ", allocationSize = 1)
    @Column(name = "OTP_ID")
    private long otpId;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "MOBILENO")
    private String mobileNo;

    @Column(name = "OTP_VALUE")
    private String otpValue;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "EXPIRE_DATE")
    private LocalDateTime expireDate;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "OTP_TYPE")
    private String otpType;
}
