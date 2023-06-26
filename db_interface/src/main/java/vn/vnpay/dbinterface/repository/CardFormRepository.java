package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.CardFormEntity;

@Repository
public interface CardFormRepository extends JpaRepository<CardFormEntity, String> {
}
