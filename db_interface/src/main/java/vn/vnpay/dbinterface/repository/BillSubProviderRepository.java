package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.BillSubProvider;

import java.util.ArrayList;

@Repository
public interface BillSubProviderRepository extends JpaRepository<BillSubProvider, String> {
    @Override
    ArrayList<BillSubProvider> findAll();

    ArrayList<BillSubProvider> findAllByBillProviderCodeAndStatus(String providerCode, String status);
}
