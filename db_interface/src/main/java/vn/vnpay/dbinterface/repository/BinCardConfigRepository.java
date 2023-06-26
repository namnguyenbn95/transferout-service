package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vnpay.dbinterface.entity.BinCard;

import java.util.List;

public interface BinCardConfigRepository extends JpaRepository<BinCard, String> {

    List<BinCard> findByStatus(String status);
}
