package vn.vnpay.dbinterface.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "MB_BENEFICIARY_BANK")
public class MbBeneficiaryBank {
    @Id
    @Column(name = "BANK_CODE")
    private String bankCode;

    @Column(name = "BANK_NAME_VN")
    private String bankName;

    @Column(name = "BANK_NAME_EN")
    private String bankNameEn;

    @Column(name = "BANK_GROUP_CENTRAL")
    private String bankGroupCentral;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "FAST_TRANFER")
    private String fastTransfer;

    @Column(name = "SAL_BANK_FULL_NAME")
    private String bankFullName;

    @Column(name = "SAL_BANK_SHORT_NAME")
    private String bankShortName;
}
