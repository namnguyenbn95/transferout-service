package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbConfig;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbConfigRepository extends JpaRepository<MbConfig, String> {

    //@Cacheable(cacheManager = "redisCacheManager", value = "mb_configs", unless = "#result.size() == 0")
    List<MbConfig> findByStatus(String status);

    //@Cacheable(cacheManager = "redisCacheManager", value = "mb_config", unless = "#result == null")
    Optional<MbConfig> findByCodeAndStatus(String code, String status);
}
