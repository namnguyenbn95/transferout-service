package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeChangePassword;

import java.util.List;

@Repository
public interface SmeChangePasswordRepository extends JpaRepository<SmeChangePassword, Long> {
    List<SmeChangePassword> findByUsername(String username);
}
