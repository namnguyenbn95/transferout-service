package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbBanner;

import java.util.List;

@Repository
public interface MbBannerRepository extends JpaRepository<MbBanner, Long> {

    @Cacheable(cacheManager = "redisCacheManager", value = "mb_banners", unless = "#result.size() == 0")
    List<MbBanner> findByStatus(String status);

    //@Cacheable(cacheManager = "redisCacheManager", value = "mb_banners", unless = "#result.size() == 0")
    List<MbBanner> findByStatusAndDisplayChannel(String status, String displayChanel);
}
