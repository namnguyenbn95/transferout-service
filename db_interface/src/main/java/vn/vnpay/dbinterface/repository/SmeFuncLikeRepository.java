package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeFuncLike;

import java.util.List;

@Repository
public interface SmeFuncLikeRepository extends JpaRepository<SmeFuncLike, Long> {
//    @Cacheable(cacheManager = "redisCacheManager", value = "mb_services", unless = "#result.size() == 0")
//    List<MbService> findByStatus(String status);

    //List<SmeFuncLike> findByServiceCodeIn(List<String> listServiceCode);

    //List<SmeFuncLike> findByUserNameAndStatusAndChannel(String userName,int status,String channel);

    List<SmeFuncLike> findByUserNameAndStatus(String userName, int status);

    List<SmeFuncLike> findByUserNameAndStatusAndChannel(String userName, int status, String channel);

    List<SmeFuncLike> findByUserNameAndChannel(String userName, String channel);

    List<SmeFuncLike> findByUserNameAndStatusAndChannelOrderById(String userName, int status, String channel);
}
