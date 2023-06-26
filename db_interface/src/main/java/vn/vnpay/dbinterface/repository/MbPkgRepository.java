package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgs;

import java.util.List;

@Repository
public interface MbPkgRepository extends JpaRepository<MbPkgs, String> {
    List<MbPkgs> findByPkgCodeAndStatus(String pkgCode,String status);
}
