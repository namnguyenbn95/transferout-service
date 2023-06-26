package vn.vnpay.dbinterface.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.BillProvider;

import java.util.ArrayList;

@Repository
public interface BillProviderRepository extends JpaRepository<BillProvider, String> {
    ArrayList<BillProvider> findAll(Sort sort);

    ArrayList<BillProvider> findAllByBillServiceCodeAndStatusOrderByOrderNumber(
            String serviceCode, String status);

    ArrayList<BillProvider> findAllByProviderAutoDebitAndStatusOrderByOrderNumber(
            String providerAutoDebit, String status);

    ArrayList<BillProvider> findAllByProviderAutoDebitOrderByOrderNumber(
            String providerAutoDebit);
}
