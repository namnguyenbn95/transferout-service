package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeCheckerServiceRole;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface SmeCheckerServiceRoleRepository extends JpaRepository<SmeCheckerServiceRole, Long> {

    List<SmeCheckerServiceRole> findByCusUserId(long cusUserId);

    List<SmeCheckerServiceRole> findByCifAndServiceCodeIn(String cif, List<String> listServiceCode);

    Optional<SmeCheckerServiceRole> findByCusUserIdAndServiceCode(long cusUserId, String serviceCode);

    Optional<SmeCheckerServiceRole> findByCusUserIdAndServiceCodeAndMakerUser(long cusUserId, String serviceCode, String maker);

    List<SmeCheckerServiceRole> findByCifAndStatus(String cif, String status);

    List<SmeCheckerServiceRole> findByAdminUserAndMakerUser(String admin, String maker);

    List<SmeCheckerServiceRole> findByAdminUser(String admin);

    List<SmeCheckerServiceRole> findByAdminUserAndCusUsername(String admin, String cusUsername);

    List<SmeCheckerServiceRole> findByCifAndServiceCodeNotNull(String cif);

    List<SmeCheckerServiceRole> findByCusUsernameAndServiceCodeNotNull(String cusUsername);

    List<SmeCheckerServiceRole> findByMakerUser(String makerUser);

    List<SmeCheckerServiceRole> findByCusUsername(String cusUserName);

    List<SmeCheckerServiceRole> findByCusUsernameAndMakerUserAndIsTransAndServiceCodeAndStatus(String cusUser, String makerUser, String isTrans, String serviceCode, String status);

    @Modifying
    @Query("UPDATE SmeCheckerServiceRole SET isTrans = :isTrans WHERE makerUser = :makerUser")
    void updateIsTransByMaker(@Param(value = "isTrans") String isTrans, @Param(value = "makerUser") String makerUser);
}
