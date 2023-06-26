package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbAccountFreeTransServ;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbAccountFreeTransServRepository extends JpaRepository<MbAccountFreeTransServ, Long> {
    Optional<MbAccountFreeTransServ> findByAccFreeTransIdAndServiceCode(Long id, String serviceCode);

    Optional<MbAccountFreeTransServ> findByAccFreeTransIdInAndServiceCode(List<Long> listId, String serviceCode);
}
