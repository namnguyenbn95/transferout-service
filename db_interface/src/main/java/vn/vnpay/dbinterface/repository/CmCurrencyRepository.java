package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.CmCurrency;

import java.util.List;

@Repository
public interface CmCurrencyRepository extends JpaRepository<CmCurrency, String> {

    @Cacheable(cacheManager = "redisCacheManager", value = "cm_currency", unless = "#result.size() == 0")
    List<CmCurrency> findByStatus(String status);

    CmCurrency findByCurrencyCodeAndStatus(String curCode, String status);
}
