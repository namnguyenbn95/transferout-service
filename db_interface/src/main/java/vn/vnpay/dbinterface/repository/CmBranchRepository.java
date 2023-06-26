package vn.vnpay.dbinterface.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.CmBranch;

import java.util.List;

@Repository
public interface CmBranchRepository extends JpaRepository<CmBranch, String> {

    @Cacheable(cacheManager = "redisCacheManager", value = "cm_branches", unless = "#result.size() == 0")
    List<CmBranch> findByBranchStatus(String status);
}
