package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_ACC_SELECT_UPDATE_BANK")
public class SmeAccSelectUpdateBank {
    @Id
    @Column(name = "TRANX_ID")
    private long id;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "METADATA")
    private String metadata;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "STATUS")
    private String statusUpdate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "COUNT")
    private Integer count;

    @Column(name = "CREATED_DATE" , nullable = false, updatable = false)
    private LocalDateTime createdDate;

}
