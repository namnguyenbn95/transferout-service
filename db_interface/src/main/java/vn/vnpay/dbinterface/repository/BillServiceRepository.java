package vn.vnpay.dbinterface.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.BillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillServiceRepository extends JpaRepository<BillService, String> {
    ArrayList<BillService> findAll(Sort sort);

    ArrayList<BillService> findAllByStatusOrderByOrderNumber(String status);

    List<BillService> findByStatusAndIsTransAndServiceCode(String status, String isTrans, String serviceCode);

    List<BillService> findByStatusAndIsDirectAndServiceCode(String status, String isDirect, String serviceCode);

    List<BillService> findByBillServiceCodeInAndIsDirect(List<String> listBillServiceCode, String isDirect);

    Optional<BillService> findByBillServiceCode(String billServiceCode);

    List<BillService> findByStatus(String status);
}
