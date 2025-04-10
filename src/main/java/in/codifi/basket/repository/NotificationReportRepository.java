package in.codifi.basket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.basket.entity.primary.NotificationReportEntity;

public interface NotificationReportRepository extends JpaRepository<NotificationReportEntity, Long> {

	/**
	 * method to find by notify id
	 * 
	 * @author SowmiyaThangaraj
	 * @param notifyId
	 * @return
	 */
	NotificationReportEntity findByNotifyId(String notifyId);

}
