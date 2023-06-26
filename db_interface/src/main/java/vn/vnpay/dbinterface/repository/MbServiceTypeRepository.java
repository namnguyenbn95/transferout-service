package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbServiceType;

import java.util.List;

@Repository
public interface MbServiceTypeRepository extends JpaRepository<MbServiceType, String> {
    @Cacheable(cacheManager = "redisCacheManager", value = "mb_service_types", unless = "#result.size() == 0")
    List<MbServiceType> findByStatus(String status);

    List<MbServiceType> findByServicetypeCodeIn(List<String> serviceTypeCodes);
}
