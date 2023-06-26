package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbMessage;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbMessageRepository extends JpaRepository<MbMessage, String> {

    @Cacheable(cacheManager = "redisCacheManager", value = "mb_messages", unless = "#result.size() == 0")
    List<MbMessage> findByStatus(String status);

    @Cacheable(cacheManager = "redisCacheManager", value = "mb_message", unless = "#result == null")
    Optional<MbMessage> findByCodeAndStatus(String code, String status);
}
