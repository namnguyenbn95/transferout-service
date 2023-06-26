package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbService;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbServiceRepository extends JpaRepository<MbService, String> {
    @Cacheable(cacheManager = "redisCacheManager", value = "mb_services", unless = "#result.size() == 0")
    List<MbService> findByStatus(String status);

    List<MbService> findByStatusAndIsTrans(String status, String isTrans);

    List<MbService> findByStatusAndIsDirect(String status, String isDirect);

    List<MbService> findByServiceCodeInAndIsDirect(List<String> listServiceCode, String isDirect);

    Optional<MbService> findByServiceCode(String serviceCode);

    List<MbService> findByServiceTypeAndStatus(String serviceType, String status);

    List<MbService> findByServiceCodeIn(List<String> listServiceCode);
}
