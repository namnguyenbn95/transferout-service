package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "MB_BILL_SERVICES")
public class BillService implements Serializable {
    @Id
    @Column(name = "BILL_SERVICE_CODE")
    String billServiceCode;

    @Column(name = "BILL_SERVICE_NAME")
    String billServiceName;

    @Column(name = "BILL_SERVICE_NAME_EN")
    String billServiceNameEn;

    @Column(name = "STATUS")
    String status;

    @Column(name = "PCM_SERVICETYPE_CODE")
    String pcmServiceCode;

    @Column(name = "SERVICETYPE_CODE")
    String serviceType;

    @Column(name = "SERVICE_CODE")
    String serviceCode;

    @Column(name = "ORDER_NUMBER")
    int orderNumber;

    @Column(name = "IS_AUTO_DEBIT_MB")
    String isAutoDebitMB;

    @Column(name = "IS_AUTO_DEBIT_IB")
    String isAutoDebitIB;

    @Column(name = "IS_ALLOW_PAY_MB")
    String isPayMB;

    @Column(name = "IS_ALLOW_PAY_IB")
    String isPayIB;

    @Column(name = "IS_ALLOW_OVERDRAFT")
    String isAllowWH;

    @Column(name = "IS_DIRECT")
    private String isDirect;

    @Column(name = "IS_TRANS")
    private String isTrans;

    @Column(name = "LABEL_TEXT_VN")
    private String labelTextVn;

    @Column(name = "LABEL_TEXT_EN")
    private String labelTextEn;

    @Transient
    ArrayList<BillProvider> providers;
}
