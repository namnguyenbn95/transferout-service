package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.common.Constants;
import vn.vnpay.dbinterface.entity.SmeEndpoint;

import java.util.List;

@Repository
@CacheConfig(cacheManager = "localCacheManager")
public interface SmeEndpointRepository extends JpaRepository<SmeEndpoint, String> {

    @Cacheable(cacheNames = Constants.CACHE_SME_ENDPOINT, unless = "#result.size() == 0")
    List<SmeEndpoint> findByStatus(String status);
}
