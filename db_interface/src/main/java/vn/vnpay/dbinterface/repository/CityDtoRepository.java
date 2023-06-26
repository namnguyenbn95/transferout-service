package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.Cities;

import java.util.List;

@Repository
public interface CityDtoRepository extends JpaRepository<Cities, String> {

    @Query(value = "select distinct cc.city_code, cc.city_name " +
            "from mb_beneficiary_bank mba " +
            "inner join mb_beneficiary_branch mbr on mba.bank_code = mbr.bene_bank_code " +
            "inner join cm_city cc on cc.city_code = mbr.city_code " +
            "where mba.bank_code = ?1 and cc.status = '1'", nativeQuery = true)
    List<Cities> getListCityByBankCode(String bankCode);

    @Query(value = "select distinct cc.city_code, cc.city_name " +
            "from cm_city cc where cc.status = '1'", nativeQuery = true)
    List<Cities> getListCity();

}
