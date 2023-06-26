package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgServiceLimit;

import java.util.Optional;

@Repository
public interface MbPkgServiceLimitRepository extends JpaRepository<MbPkgServiceLimit, Long> {

    Optional<MbPkgServiceLimit> findByPackageCodeAndServiceCodeAndStatus(String packageCode, String serviceCode, String status);
}
