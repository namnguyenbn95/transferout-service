package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "SME_CHECKSUM")
public class SmeChecksum {
    @Id
    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "CIF_CORE")
    private String cif;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "SIGN_DATA")
    private String signData;
}
