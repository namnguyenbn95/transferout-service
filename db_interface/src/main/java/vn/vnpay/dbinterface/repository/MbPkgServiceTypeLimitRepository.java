package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgServiceTypeLimit;

import java.util.List;

@Repository
public interface MbPkgServiceTypeLimitRepository extends JpaRepository<MbPkgServiceTypeLimit, Long> {

    @Query("select totalLimit from MbPkgServiceTypeLimit where packageCode = :packageCode and currency = :currency  and methodOtp=:methodOtp and servicetypeCode=:servicetypeCode")
    Long totalLimit(@Param(value = "packageCode") String packageCode, @Param(value = "currency") String currency, @Param(value = "methodOtp") String methodOtp, @Param(value = "servicetypeCode") String servicetypeCode);

    @Query(value = "select * from  mb_package_service_type_limits "
            + "where PACKAGE_CODE = ?1 and METHOD_OTP = '0' and STATUS = '1' and SERVICETYPE_CODE in ('10','11') ",
            nativeQuery = true)
    List<MbPkgServiceTypeLimit> getAllByServiceTypeCode(String packageCode);
}
