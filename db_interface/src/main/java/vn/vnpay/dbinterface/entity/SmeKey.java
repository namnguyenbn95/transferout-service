package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "SME_KEY")
public class SmeKey {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idSeq_SME_KEY")
    @SequenceGenerator(name = "idSeq_SME_KEY", sequenceName = "MB_KEY_SEQ", allocationSize = 1)
    @Column(name = "KEY_ID")
    private long keyId;

    @Column(name = "CLIENT_PUBLIC_KEY")
    private String clientPublicKey;

    @Column(name = "SERVER_PRIVATE_KEY")
    private String serverPrivateKey;
}
