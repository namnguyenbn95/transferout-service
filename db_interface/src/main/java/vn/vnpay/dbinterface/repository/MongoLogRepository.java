package vn.vnpay.dbinterface.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MongoLogEntity;

@Repository
public interface MongoLogRepository extends MongoRepository<MongoLogEntity, String> {
}
