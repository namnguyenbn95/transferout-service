package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "MB_CARD_FORM")
@Entity
public class CardFormEntity {
    @Id
    @Column(name = "CARD_FORM_CODE")
    String cardFormCode;

    @Column(name = "PDT_NUMBER")
    String pdtNumber;

    @Column(name = "BIN")
    String bin;

    @Column(name = "PRODUCT_NAME")
    String productName;

    @Column(name = "CARD_TYPE")
    String cardType;

    @Column(name = "CARD_FORM_URL")
    String cardFormUrl;

    @Column(name = "CARD_SIZE")
    String cardSize;
}
