package vn.vnpay.dbinterface.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.vnpay.dbinterface.entity.MongoSmeOTTLogEntity;

public interface MongoSmeOTTLogRepository extends MongoRepository<MongoSmeOTTLogEntity, String> {

}
