package vn.vnpay.dbinterface.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "MB_BENEFICIARY_BRANCH")
public class MbBeneficiaryBranch {
    @Id
    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "BRANCH_NAME_VN")
    private String branchNameVn;

    @Column(name = "BRANCH_NAME_EN")
    private String branchNameEn;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "BENE_BANK_CODE")
    private String beneBankCode;

    @Column(name = "CITY_CODE")
    private String cityCode;

    @Column(name = "SAL_BRANCH_NAME_OTHER")
    private String branchNameOther;

}
