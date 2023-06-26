package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbBlacklist;

import java.util.List;

@Repository
public interface MbBlacklistRepository extends JpaRepository<MbBlacklist, Long> {
    List<MbBlacklist> findByStatus(String status);
}
