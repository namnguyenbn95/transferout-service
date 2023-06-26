package vn.vnpay.dbinterface.reposotorydboffline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entitydboffline.SmeTransOffline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SmeTransRepositoryOffline extends JpaRepository<SmeTransOffline, Long> {
    List<SmeTransOffline> findByTranxTimeBetweenAndCreatedUserAndCifNo(LocalDateTime from, LocalDateTime to, String createdUser, String cifNo);

    List<SmeTransOffline> findByTranxTimeBetweenAndCifNo(LocalDateTime from, LocalDateTime to, String cif);

    List<SmeTransOffline> findByTranxTimeBetweenAndCifInt(LocalDateTime from, LocalDateTime to, int cifNo);

    Optional<SmeTransOffline> findByTranxTimeBetweenAndTsolRef(LocalDateTime from, LocalDateTime to, String tsolRef);

    Optional<SmeTransOffline> findByTsolRef(String tsolRef);
}
