package vn.vnpay.dbinterface.repository.batch_salary;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.vnpay.dbinterface.entity.batch_salary.TemplateFixed;

import java.util.List;


public interface TemplateFixedRepository extends MongoRepository<TemplateFixed, String> {

    List<TemplateFixed> findByCreatedUser(String username);

    List<TemplateFixed> findByCifOrderByCreatedTimeDesc(String cif);

    List<TemplateFixed> findByIdIn(List<String> ids);

    void deleteAllByCifAndIdIn(String cif, List<String> ids);
}
