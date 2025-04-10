package in.codifi.basket.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.codifi.basket.entity.primary.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

	@Transactional
	@Query(value = "SELECT a FROM tbl_user_notification a where a.userId =:userId or a.userType ='ALL' and a.activeStatus ='1'")
	List<UserNotification> getNotificationForUser(@Param("userId") String userId);

	/**
	 * method to find by notification id
	 * 
	 * @author SowmiyaThangaraj
	 * @param notifyId
	 * @return
	 */
	UserNotification findByNotifyId(String notifyId);

}
