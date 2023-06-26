package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.vnpay.dbinterface.entity.SendOTTEntity;

import java.util.List;

public interface SendOTTRepository extends JpaRepository<SendOTTEntity, Long> {
    @Query(value = "select DISTINCT rownum as id, a.user_name, a.mobile_otp, b.is_view, b.acc_type from sme_customers_users a left join sme_user_acc_role b "
            + "on a.user_name =  b.cus_username and a.cif_int = b.cif and b.cif = ?1 and b.status = '1' and b.acc_no = ?2 "
            + "where a.cus_user_status = '3' and a.cif_int = ?1 and a.authen_notify = '1' and a.balance_notify = '1' ", nativeQuery = true)
    List<SendOTTEntity> getListUserSendOTT(Integer cifInt, String accNo);
}
