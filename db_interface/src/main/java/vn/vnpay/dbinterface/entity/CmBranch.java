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
@Table(name = "CM_BRANCHES")
public class CmBranch {

    @Id
    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @Column(name = "CITY_CODE")
    private String cityCode;

    @Column(name = "DISTRICT_CODE")
    private String districtCode;

    @Column(name = "BRANCH_ADDRESS")
    private String branchAddress;

    @Column(name = "BRANCH_STATUS")
    private String branchStatus;

    @Column(name = "VAT_NH")
    private String vat;
}
