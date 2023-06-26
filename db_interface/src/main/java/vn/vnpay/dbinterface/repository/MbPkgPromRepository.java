package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgProm;

import java.util.Optional;

@Repository
public interface MbPkgPromRepository extends JpaRepository<MbPkgProm, String> {
    Optional<MbPkgProm> findByPromCodeAndPkgCodeAndStatus(String promCode, String pkgCode, String status);
}
