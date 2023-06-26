package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeOtp;

import java.util.Optional;

@Repository
public interface SmeOtpRepository extends JpaRepository<SmeOtp, Long> {
    Optional<SmeOtp> findByOtpIdAndUsernameAndOtpTypeAndStatus(Long otpId, String username, String otpType, String status);
}
