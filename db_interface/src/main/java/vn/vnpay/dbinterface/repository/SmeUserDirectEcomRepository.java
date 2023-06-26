package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeUserDirectEcom;

import java.util.List;

@Repository
public interface SmeUserDirectEcomRepository extends JpaRepository<SmeUserDirectEcom, Long> {
    List<SmeUserDirectEcom> findByCifAndAdminUserOrderByCreatedDateDesc(String cif, String admin);

    List<SmeUserDirectEcom> findByCusUserIdAndMidAndTid(long cusUserId, String mid, String tid);

    List<SmeUserDirectEcom> findByCusUsernameAndCif(String username, String cif);

    void deleteAllByCusUsernameAndMidAndTid(String cusUsername, String mid, String tid);
}
