package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "SME_FUNC_LIKE")
public class SmeFuncLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sme_func_like_seq")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "sme_func_like_seq", sequenceName = "sme_func_like_seq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "DISPLAY_ID")
    private int displayHomeId;

    @Column(name = "STATUS")
    private int status;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;


    @Transient
    private int countAct;
    @Transient
    private String serviceGroup;
    @Transient
    private String serviceGroupName;
    @Transient
    private String serviceName;
    @Transient
    private String serviceTypeCode;
    @Transient
    private String serviceTypeName;
    @Transient
    private String isLike;
    @Transient
    private String isSuggest;
    @Transient
    private String isLastUsed;
    @Transient
    private String type; //1. Gợi ý, 2 gần đây, 3 yêu thích
    @Transient
    private String billServiceCode;
    @Transient
    private String billServiceName;
    @Transient
    private String srvGroupName;
}
