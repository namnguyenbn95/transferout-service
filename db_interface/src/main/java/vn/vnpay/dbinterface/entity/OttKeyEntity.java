package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "OTT_KEYS")
public class OttKeyEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ott_key_id_seq_name")
    @SequenceGenerator(name = "ott_key_id_seq_name", sequenceName = "ott_key_id_seq", allocationSize = 1)
    long id;

    @Column(name = "CREATED_DATE")
    Timestamp createdDate;

    @Column(name = "MOBILE_NO")
    String mobileNo;

    @Column(name = "KEY_VALUE")
    String keyValue;
}
