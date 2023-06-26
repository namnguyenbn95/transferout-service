package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.vnpay.dbinterface.entity.LimitTransEntity;

import java.util.List;

public interface LimitTransRepository extends JpaRepository<LimitTransEntity, Long> {
    @Query(value = "select a.PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID, a.SERVICETYPE_CODE, a.LIMIT_CUSTOMER, a.DEFAULT_LIMIT, a.CURRENCY, a.PACKAGE_CODE, a.SUB_SERVICE_CODE "
            + "from mb_package_sub_service_type_limits a INNER join sme_customers_sub_service_type_limit b "
            + "ON a.PACKAGE_CODE = b.PACKAGE_CODE and a.SERVICETYPE_CODE = b.SERVICETYPE_CODE and a.SUB_SERVICE_CODE = b.SUB_SERVICE_CODE "
            + "where b.cus_id =?1 and  a.SERVICETYPE_CODE in ('10','11') ", nativeQuery = true)
    List<LimitTransEntity> getListTransCus(long cusId);

    @Query(value = "select a.PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID, a.SERVICETYPE_CODE, a.LIMIT_CUSTOMER, a.DEFAULT_LIMIT, a.CURRENCY, a.PACKAGE_CODE, a.SUB_SERVICE_CODE "
            + "from mb_package_sub_service_type_limits a  "
            + "where SERVICETYPE_CODE in ('10','11') and SUB_SERVICE_CODE in ?1 and PACKAGE_CODE = ?2", nativeQuery = true)
    List<LimitTransEntity> getAllListTransSub(List<String> listServiceTypeCode, String pckCode);

    @Query(value = "select a.PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID, a.SERVICETYPE_CODE, a.LIMIT_CUSTOMER, a.DEFAULT_LIMIT, a.CURRENCY, a.PACKAGE_CODE, a.SUB_SERVICE_CODE "
            + "from mb_package_sub_service_type_limits a  "
            + "where SERVICETYPE_CODE in ('10','11') and a.status = '1' and SUB_SERVICE_CODE in ?1 and PACKAGE_CODE = ?2", nativeQuery = true)
    List<LimitTransEntity> getListTransSubActive(List<String> listServiceTypeCode, String pckCode);

    @Query(value = "select a.PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID, a.SERVICETYPE_CODE, a.LIMIT_CUSTOMER, a.DEFAULT_LIMIT, a.CURRENCY, a.PACKAGE_CODE, a.SUB_SERVICE_CODE "
            + "from mb_package_sub_service_type_limits a  "
            + "where SERVICETYPE_CODE in ('10','11') and a.status = '0' and SUB_SERVICE_CODE in ?1 and PACKAGE_CODE = ?2", nativeQuery = true)
    List<LimitTransEntity> getListTransSubInActive(List<String> listServiceTypeCode, String pckCode);

    @Query(value = "select a.PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID, a.SERVICETYPE_CODE, a.LIMIT_CUSTOMER, a.DEFAULT_LIMIT, a.CURRENCY, a.PACKAGE_CODE, a.SUB_SERVICE_CODE "
            + "from mb_package_sub_service_type_limits a  "
            + "where SERVICETYPE_CODE in ('10','11') and a.package_sub_service_type_limit_id not in ?1 ", nativeQuery = true)
    List<LimitTransEntity> getListTransSubNot(List<String> id);

    @Modifying
    @Query(value = "DELETE FROM sme_customers_sub_service_type_limit WHERE " +
            "cus_id = ?1 and PACKAGE_CODE = ?2 and SERVICETYPE_CODE = ?3 and SUB_SERVICE_CODE = ?4 ", nativeQuery = true)
    void deleteCusSubServiceType(long cusId, String packageCode, String serviceTypeCode, String subServiceCode);

    @Modifying
    @Query(value = "insert into sme_customers_sub_service_type_limit values "
            + "(?1, ?2, ?3, ?4) ", nativeQuery = true)
    void insertCusSubServiceType(long cusId, String pkgCode, String serviceTypeCode, String subServiceCode);
}
