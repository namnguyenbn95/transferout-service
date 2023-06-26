package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbBeneficiaryBank;

import java.util.List;

@Repository
public interface MbBeneficiaryBankRepository extends JpaRepository<MbBeneficiaryBank, String> {
    List<MbBeneficiaryBank> findByStatus(String status);

    List<MbBeneficiaryBank> findByStatusAndFastTransfer(String status, String fastTransfer);

    List<MbBeneficiaryBank> findByBankName(String bankName);

    List<MbBeneficiaryBank> findByBankCode(String bankCode);

    List<MbBeneficiaryBank> findByBankNameAndFastTransfer(String bankName, String fastTransfer);

    List<MbBeneficiaryBank> findByBankCodeAndFastTransfer(String bankCode, String fastTransfer);

    @Query(value = "select * from MB_BENEFICIARY_BANK " +
            "where (FAST_TRANFER = '0' and SALARY_TRANSFER = '1' and " +
            "(BANK_NAME_VN = :bankName or SAL_BANK_FULL_NAME = :bankName or SAL_BANK_SHORT_NAME = :bankName)) " +
            "or (FAST_TRANFER = '0' and (SALARY_TRANSFER is null or SALARY_TRANSFER != '1') and BANK_NAME_VN = :bankName)"
            , nativeQuery = true)
    List<MbBeneficiaryBank> findBankName(String bankName);
}
