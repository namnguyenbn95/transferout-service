package vn.vnpay.dbinterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

@Slf4j
@Service
public class AuthenService {
    @Autowired
    @Qualifier("dbOnEntityManager")
    EntityManager entityManager;

    /**
     * Hủy user maker, checker
     *
     * @param cusUserId
     * @param updateUser
     * @return
     */
    public void cancelCustomerUser(Long cusUserId, String updateUser) {
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("pkg_sme_auth.cancel_customer_user")
                    .registerStoredProcedureParameter("p_cus_user_id", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_update_user", String.class, ParameterMode.IN)
                    .setParameter("p_cus_user_id", cusUserId)
                    .setParameter("p_update_user", updateUser);
            query.execute();
        } catch (Exception ex) {
            log.info("cancel customer user ex: ", ex);
        }
    }

    public String createCustomerUser(
            Long cusId,
            String mobileOtp,
            String email,
            String authenMethod,
            String serialNumber,
            String roleType,
            String registCode,
            String branch,
            String pos,
            String createUser,
            String source,
            String username,
            String pin,
            String checksum,
            long cusUserId) {
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_be_customer_user.proc_cu_add_mb")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_mobile_otp", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_serial_number", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_regist_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_branch", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pos", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_regist_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pin", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_hash", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN)
                    .setParameter("p_id", cusId)
                    .setParameter("p_mobile_otp", mobileOtp)
                    .setParameter("p_email", email)
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_serial_number", serialNumber)
                    .setParameter("p_role_type", roleType)
                    .setParameter("p_regist_code", registCode)
                    .setParameter("p_branch", branch)
                    .setParameter("p_pos", pos)
                    .setParameter("p_user", createUser)
                    .setParameter("p_regist_channel", source)
                    .setParameter("p_user_name", username)
                    .setParameter("p_pin", pin)
                    .setParameter("p_hash", checksum)
                    .setParameter("p_user_id", cusUserId);
            query.execute();
            return (String) query.getOutputParameterValue("p_out");
        } catch (Exception ex) {
            log.info("create customer user ex: ", ex);
            return null;
        }
    }
}
