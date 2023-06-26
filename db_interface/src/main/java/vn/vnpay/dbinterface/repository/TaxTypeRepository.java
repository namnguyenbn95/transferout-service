package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.TaxType;

import java.util.List;

@Repository
public interface TaxTypeRepository extends JpaRepository<TaxType, String> {
    List<TaxType> findByStatus(String status);
}
