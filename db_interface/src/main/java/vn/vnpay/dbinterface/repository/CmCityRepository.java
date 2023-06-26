package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.CmCity;

import java.util.List;

@Repository
public interface CmCityRepository extends JpaRepository<CmCity, String> {
    List<CmCity> findByStatus(String status);

    List<CmCity> findByCityCode(String city);
}
