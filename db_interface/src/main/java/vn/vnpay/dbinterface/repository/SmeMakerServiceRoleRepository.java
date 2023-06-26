package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeMakerServiceRole;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface SmeMakerServiceRoleRepository extends JpaRepository<SmeMakerServiceRole, Long> {

    List<SmeMakerServiceRole> findByCusUserId(long cusUserId);

    List<SmeMakerServiceRole> findByCifAndServiceCodeIn(String cif, List<String> listServiceCode);

    Optional<SmeMakerServiceRole> findByCusUserIdAndAccNo(long cusUserId, String accNo);

    List<SmeMakerServiceRole> findByCusUsernameAndAccNoNotNull(String cusUsername);

    List<SmeMakerServiceRole> findByCusUsernameAndServiceCodeNotNull(String cusUsername);

    @Query("SELECT m FROM SmeMakerServiceRole m WHERE m.cusUserId = :cusUserId AND m.isTrans = '1' AND m.status = '1' AND (m.accNo = :accNo OR m.accAlias = :accNo)")
    Optional<SmeMakerServiceRole> validateTransAuthorityForAccount(@Param("cusUserId") long cusUserId, @Param("accNo") String accNo);

    Optional<SmeMakerServiceRole> findByCusUserIdAndServiceCode(long cusUserId, String serviceCode);

    @Modifying
    @Query("UPDATE SmeMakerServiceRole SET status = :status WHERE cusUserId = :cusUserId")
    void updateStatusByCusUserId(@Param(value = "status") String status, @Param(value = "cusUserId") long cusUserId);

    List<SmeMakerServiceRole> findByCifAndServiceCodeNotNull(String cif);

    @Query("select DISTINCT cusUserId, cusUsername from SmeMakerServiceRole where cif = :cif and adminUser = :admin")
    List<Object[]> findByCifAndAdminUserDistinctCusUserId(@Param(value = "cif") String cif, @Param(value = "admin") String admin);

    List<SmeMakerServiceRole> findByCifAndAdminUser(String cif, String admin);

    List<SmeMakerServiceRole> findByCifAndStatus(String cif, String status);

    List<SmeMakerServiceRole> findByCusUsernameAndIsTrans(String cusUserName, String isTrans);
}
