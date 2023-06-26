package vn.vnpay.dbinterface.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SME_BATCH_TRANSFER")
public class BatchTransfer implements Serializable {
    @Id
    @Column(name = "FILE_ID")
    Long fileId;

    @Column(name = "FILE_NAME")
    String fileName;

    @Column(name = "CREATED_DATE")
    LocalDateTime createdDate;

    @Column(name = "CREATED_USER")
    String createdUser;

    @Column(name = "CONFIRM_DATE")
    LocalDateTime confirmDate;

    @Column(name = "CONFIRM_USER")
    String confirmUser;

    @Column(name = "FROM_ACC")
    String fromAcc;

    @Column(name = "STATUS")
    String status;

    @Column(name = "FILE_PATH")
    String filePath;

    @Column(name = "CIF_NO")
    String cifNo;

    @Column(name = "REF_NO")
    String refNo;

    @Column(name = "TRANS_DATE")
    String transDate;

    @Column(name = "CONTENT")
    String content;

    @Column(name = "METADATA")
    String metaData;

    @Column(name = "REASON")
    String reason;

    @Column(name = "TOTAL_ITEM")
    Long totalItem;

    @Column(name = "TOTAL_AMOUNT")
    BigDecimal totalAmount;

    @Column(name = "TOTAL_FEE")
    BigDecimal totalFee;

    @Column(name = "CCY")
    String ccy;

    @Transient
    private Long numberTrue;
}
