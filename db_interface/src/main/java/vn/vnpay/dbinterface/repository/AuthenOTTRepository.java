package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.vnpay.dbinterface.entity.AuthenOTTEntity;

import java.util.List;

public interface AuthenOTTRepository extends JpaRepository<AuthenOTTEntity, Long> {
    @Query(value = "select DISTINCT a.user_name, a.mobile_otp, a.email, a.role_type, a.authen_notify from sme_customers_users a left join sme_user_acc_role b "
            + "on a.user_name =  b.cus_username and a.cif_int = b.cif and b.admin_user = ?1 "
            + " where a.cif_int = ?2 and a.role_type != '3' and (status is Null or status != '0') and (is_view is null or  b.is_view != '0') "
            + " order by user_name", nativeQuery = true)
    List<AuthenOTTEntity> getListAuthenOtt(String username, String cif);
}
