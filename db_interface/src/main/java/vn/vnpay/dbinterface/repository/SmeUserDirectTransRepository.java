package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeUserDirectTrans;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmeUserDirectTransRepository extends JpaRepository<SmeUserDirectTrans, Long> {
    List<SmeUserDirectTrans> findByCusUserId(long cusUserId);

    List<SmeUserDirectTrans> findByCifAndAdminUserOrderByCreatedDateDesc(String cif, String admin);

    List<SmeUserDirectTrans> findByCusUserIdAndServiceCode(long cusUserId, String serviceCode);

    void deleteByCusUsernameAndServiceCode(String cusUsername, String serviceCode);

    @Query("SELECT m FROM SmeUserDirectTrans m WHERE m.cusUsername = :username AND m.serviceCode = :serviceCode AND (m.accNo = :accNo OR m.accAlias = :accNo)")
    Optional<SmeUserDirectTrans> checkDirectTrans(String username, String serviceCode, String accNo);
}
