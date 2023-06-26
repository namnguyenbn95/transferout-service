package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MBHoliday;

import java.util.List;

@Repository
public interface MBHolidayRepository extends JpaRepository<MBHoliday, Integer> {
    List<MBHoliday> findByStatus(String status);
}
