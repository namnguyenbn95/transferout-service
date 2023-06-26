package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeClient;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmeClientRepository extends JpaRepository<SmeClient, Long> {
    Optional<SmeClient> findByCusUserId(long cusUserId);

    List<SmeClient> findByCusUserIdIn(List<Long> cusIds);

    void deleteByCusUserId(long cusUserId);
}
