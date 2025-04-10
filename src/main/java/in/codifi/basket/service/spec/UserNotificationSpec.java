package in.codifi.basket.service.spec;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import in.codifi.basket.entity.primary.UserNotification;
import in.codifi.basket.model.request.SendNoficationReqModel;

public interface UserNotificationSpec {

	void saveNotification(SendNoficationReqModel reqModel) throws JsonProcessingException;
	
	List<UserNotification> getNotificationList(String userId);
}
