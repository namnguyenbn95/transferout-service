package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_BANNER")
public class MbBanner implements Serializable {

    private static final long serialVersionUID = 6911500198477826014L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "BANNER_NAME")
    private String bannerName;

    @Column(name = "DISPLAY_CHANNEL")
    private String displayChannel;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "IMG_URL")
    private String imgUrl;

    @Column(name = "HANDLE_TYPE")
    private String handleType;

    @Column(name = "SERVICE_CODE_NAV")
    private String serviceCodeNav;

    @Column(name = "LINK_URL_NAV")
    private String linkUrlNav;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "CREATED_USER")
    private String createdUser;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "UPDATED_USER")
    private String updatedUser;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ORDINAL")
    private String order;
}
