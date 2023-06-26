package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbAccountSerPkg;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbAccountSerPkgRepository extends JpaRepository<MbAccountSerPkg, String> {
    List<MbAccountSerPkg> findByPkgCodeAndStatus(String pkgCode, String status);

    List<MbAccountSerPkg> findByStatus(String status);

    Optional<MbAccountSerPkg> findByStatusAndPkgCode(String status, String pkgCode);

    Optional<MbAccountSerPkg> findByPkgCode(String pkgCode);
}
