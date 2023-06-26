package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "MB_LUCKY_ACCOUNT_NUMBER_TYPE")
public class LuckyAccNumType implements Serializable {
    @Id
    @Column(name = "TYPE_CODE")
    String typeCode;

    @Column(name = "TYPE_NAME_VN")
    String typeName;

    @Column(name = "TYPE_NAME_EN")
    String typeNameEn;

    @Column(name = "DESCRIPTION_VN")
    String description;

    @Column(name = "DESCRIPTION_EN")
    String descriptionEn;

    @Column(name = "ORDER_NO")
    Integer orderNo;

    @Column(name = "DISPLAY_IN_LIST")
    String displayInList;

    @Column(name = "STATUS")
    String status;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "CREATED_USER")
    String createdUser;

    @Column(name = "UPDATED_USER")
    String updatedUser;

}
