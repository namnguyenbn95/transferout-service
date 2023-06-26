package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.BatchTransfer;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatchTransferRepository extends JpaRepository<BatchTransfer, String> {
    List<BatchTransfer> findByCreatedUserAndCreatedDateBetween(String createUser, LocalDateTime from, LocalDateTime to);
    BatchTransfer findByFileId(Long id);
    List<BatchTransfer> findByStatus(String s);
}
