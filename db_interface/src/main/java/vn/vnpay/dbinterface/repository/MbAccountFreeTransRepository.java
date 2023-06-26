package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbAccountFreeTrans;

import java.util.List;

@Repository
public interface MbAccountFreeTransRepository extends JpaRepository<MbAccountFreeTrans, Long> {
    List<MbAccountFreeTrans> findByAccountNoInAndStatus(List<String> acccountLst, String status);
}


