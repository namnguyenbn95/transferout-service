package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbBeneficiaryBranch;

import java.util.List;

@Repository
public interface MbBeneficiaryBranchRepository extends JpaRepository<MbBeneficiaryBranch, String> {
    List<MbBeneficiaryBranch> findByStatus(String status);

    List<MbBeneficiaryBranch> findByBeneBankCodeAndCityCode(String bankCode, String cityCode);

    List<MbBeneficiaryBranch> findByBranchCode(String branhCode);

    List<MbBeneficiaryBranch> findByBranchNameVn(String branhName);

    @Query(value = "select * from MB_BENEFICIARY_BRANCH " +
            "where (SALARY_TRANSFER = '1' and " +
            "(BRANCH_NAME_VN = :branchName or SAL_BRANCH_NAME_OTHER = :branchName)) " +
            "or ((SALARY_TRANSFER is null or SALARY_TRANSFER != '1') and BRANCH_NAME_VN=:branchName)"
            , nativeQuery = true)
    List<MbBeneficiaryBranch> findBranchName(String branchName);
}
