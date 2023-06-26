package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeFuncRecent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SmeFuncRecentRepository extends JpaRepository<SmeFuncRecent, Long> {
    @Query(value = "select b.SERVICE_CODE, b.SERVICE_NAME,b.SERVICE_NAME_EN,c.SERVICETYPE_NAME,c.SERVICETYPE_NAME_EN,d.SRV_GROUP_NAME,d.SRV_GROUP_NAME_EN " +
            " from SME_FUNC_RECENT a join MB_SERVICE b " +
            " on a.service_code = b.service_code join MB_SERVICE_TYPE c on b.SERVICE_TYPE = c.SERVICETYPE_CODE " +
            " join SERVICE_GROUP d on b.SERVICE_GROUP = d.SRV_GROUP " +
            " WHERE a.user_name = :userName AND a.COUNT_ACT >=3 AND b.status=1 AND c.status=1 and (:channel is null or a.CHANNEL = :channel ) " +
            " order by a.ID ", nativeQuery = true)
    List<SmeFuncRecent> find(@Param("userName") String userName, @Param("channel") String channel);

    @Query(value = " select a.* from SME_FUNC_RECENT a WHERE a.USER_NAME = ?1 AND a.CHANNEL =?2 AND a.COUNT_ACT >= ?3 order by a.updated_date desc FETCH FIRST 2 ROWS ONLY  ", nativeQuery = true)
    List<SmeFuncRecent> findByUserNameAndChannelCustom(String userName, String channel, int countAct);

    @Query(value = " select a.ID from SME_FUNC_RECENT a WHERE a.USER_NAME = ?1 AND a.CHANNEL =?2 AND a.COUNT_ACT >= ?3  order by a.updated_date desc FETCH FIRST 2 ROWS ONLY  ", nativeQuery = true)
    List<String> getIdLatestByUser(String userName, String channel, int countAct);

    @Query(value = " SELECT a.* from SME_FUNC_RECENT a WHERE a.USER_NAME = ?1 AND a.CHANNEL =?2 AND a.UPDATED_DATE < ?3 AND a.ID NOT IN (?4)  ", nativeQuery = true)
    List<SmeFuncRecent> getOldFuncByUser(String userName, String channel, LocalDate dateToday, List<String> listId);

    @Query(value = " select a.* from SME_FUNC_RECENT a WHERE a.USER_NAME = :userName AND a.DISPLAY_ID = :displayHomeId AND a.CHANNEL =:channel AND  a.UPDATED_DATE >=  :date1 "
            , nativeQuery = true)
    Optional<SmeFuncRecent> findExactFuncToday(@Param("userName") String userName, @Param("displayHomeId") int displayHomeId
            , @Param("channel") String channel, @Param("date1") LocalDate date1);

    @Query(value = " select user_name from sme_func_recent where channel = ?1 group by user_name  ", nativeQuery = true)
    List<String> getUserNameDistinct(String channel);

    @Query(value = " select * from sme_func_recent where USER_NAME=?1 AND CHANNEL = ?2 ", nativeQuery = true)
    List<SmeFuncRecent> findByUserNameAndChannel(String userName, String channel);

}
