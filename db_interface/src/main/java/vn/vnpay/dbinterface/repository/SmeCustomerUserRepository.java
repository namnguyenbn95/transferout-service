package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface SmeCustomerUserRepository extends JpaRepository<SmeCustomerUser, Long> {

    // @Query(value = "SELECT su.*,sc.CONFIRM_TYPE FROM SME_CUSTOMERS_USERS su INNER JOIN SME_CUSTOMERS sc ON su.CUS_ID=sc.CUS_ID WHERE su.USER_NAME=?1", nativeQuery = true)
    Optional<SmeCustomerUser> findByUsername(String username);

    Optional<SmeCustomerUser> findByCusUserId(long cusUserId);

    List<SmeCustomerUser> findByCifAndRoleTypeNot(String cif, String roleTypeNot);

    List<SmeCustomerUser> findByCusIdAndAuthenMethod(Long cusId, String authenMethod);

    List<SmeCustomerUser> findByCusId(Long cusId);

    List<SmeCustomerUser> findByCusUserIdIn(List<Long> cusIds);

    Optional<SmeCustomerUser> findByCusUserIdAndUsername(long cusUserId, String username);

    List<SmeCustomerUser> findByCusIdAndAuthenMethodAndRoleTypeNot(Long cusId, String authenMethod, String roleType);

    List<SmeCustomerUser> findByCifAndRoleType(String cif, String roleType);

    List<SmeCustomerUser> findByCifAndRoleTypeIn(String cif, List<String> roleTypes);

    long countByCif(String cif);

    List<SmeCustomerUser> findByCifAndMobileOtpIn(String cif, List<String> mobiles);

    Optional<SmeCustomerUser> findByCusIdAndMobileOtp(Long cusId, String mobileOtp);

    Optional<SmeCustomerUser> findByCusIdAndEmail(Long cusId, String email);

    List<SmeCustomerUser> findByUsernameIn(Set<String> setUsername);

    List<SmeCustomerUser> findByCif(String cif);

    @Modifying
    @Query("update SmeCustomerUser c set c.activatedSmartOtp = :status where c.username = :username")
    void updateSoftStatus(String username, String status);

    @Modifying
    @Query("update SmeCustomerUser c set c.pinExpireDate = :pinExpireDate, c.pin = :pin, c.registCode = :registCode, c.cusUserStatus = :cusUserStatus, c.activatedSmartOtp = '0' where c.username = :username")
    void updateNewPinAndRegisCode(String username, String cusUserStatus, String pin, String registCode, LocalDateTime pinExpireDate);

    @Modifying
    @Query(value = "update sme_customers_users c set c.authen_notify = :status where c.user_name = :username", nativeQuery = true)
    void updateAuthenNotify(@Param("username") String username, @Param("status") String status);

    @Modifying
    @Query(value = "update sme_customers_users c set c.balance_notify = :status where c.user_name = :username", nativeQuery = true)
    void updateStatusNotify(@Param("username") String username, @Param("status") String status);

    List<SmeCustomerUser> findByEmail(String email);

    @Query(value = "SELECT sme_customers_users_seq.nextval FROM dual", nativeQuery = true)
    public BigDecimal getNextValCusUserSeq();

    List<SmeCustomerUser> findByCusIdAndAuthenMethodAndRoleTypeNotAndCusUserStatusIn(Long cusId, String authenMethod, String roleType, List<String> status);

    List<SmeCustomerUser> findByEmailAndCifAndCusUserStatusIsNot(String email, String cif, String cusUserStatus);

    List<SmeCustomerUser> findByCifIntAndRoleType(int cifInt, String roleType);
}
