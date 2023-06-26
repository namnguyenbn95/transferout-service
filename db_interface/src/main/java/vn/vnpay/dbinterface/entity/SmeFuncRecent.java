package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_FUNC_RECENT")
public class SmeFuncRecent {

    @Id
    @SequenceGenerator(name = "sme_func_recent_seq", sequenceName = "sme_func_recent_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sme_func_recent_seq")
    private Long id;

    @Column(name = "DISPLAY_ID")
    private int displayHomeId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "COUNT_ACT")
    private Integer countAct;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updateDate;

    @Transient
    private String displayLevel;
    @Transient
    private String type; //1. Gợi ý, 2 gần đây, 3 yêu thích

}
