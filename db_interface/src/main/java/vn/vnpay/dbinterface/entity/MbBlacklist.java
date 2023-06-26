package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "MB_BLACKLIST")
public class MbBlacklist {
    @Id
    @Column(name = "BLACKLIST_ID")
    private long id;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @Column(name = "RES_CODE")
    private String resCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;
}
