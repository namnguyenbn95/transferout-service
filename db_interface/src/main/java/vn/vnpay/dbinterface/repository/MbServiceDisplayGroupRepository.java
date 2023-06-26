package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbServiceDisplayGroup;

import java.util.List;
import java.util.Set;

@Repository
public interface MbServiceDisplayGroupRepository extends JpaRepository<MbServiceDisplayGroup, Long> {
    List<MbServiceDisplayGroup> findByIdIn(Set<Long> groupIds);
}
