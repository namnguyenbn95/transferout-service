package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeLimitMaker;

import java.util.List;

@Repository
public interface SmeLimitMakerRepository extends JpaRepository<SmeLimitMaker, Long> {

    List<SmeLimitMaker> findByUserCreated(String userCreated);

}
