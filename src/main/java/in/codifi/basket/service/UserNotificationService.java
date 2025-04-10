package in.codifi.basket.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.basket.entity.primary.UserNotification;
import in.codifi.basket.model.request.SendNoficationReqModel;
import in.codifi.basket.repository.UserNotificationRepository;
import in.codifi.basket.service.spec.UserNotificationSpec;
import in.codifi.basket.utils.AppConstants;
import io.quarkus.logging.Log;

@ApplicationScoped
public class UserNotificationService implements UserNotificationSpec {

	@Inject
	UserNotificationRepository userNotificationRepo;

	/**
	 *
	 */
	@Override
	public void saveNotification(SendNoficationReqModel reqModel) throws JsonProcessingException {

		if (AppConstants.ALL.equalsIgnoreCase(reqModel.getUserType())) {
			dbOperation(AppConstants.ALL, reqModel);

		} else {
			List<String> users = Arrays.asList(reqModel.getUserId());
			int numThreads = users.size(); // Number of threads = number of users
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			for (String user : users) {
				executor.submit(() -> {
					try {
						dbOperation(user, reqModel);
					} catch (JsonProcessingException e) {
						Log.error(e);
					}
				});
			}
			executor.shutdown();
		}

	}

	private void dbOperation(String user, SendNoficationReqModel reqModel) throws JsonProcessingException {
		UserNotification entity = buildUserNotification(user, reqModel);
		try {
			userNotificationRepo.save(entity);
		} catch (Exception e) {
			Log.error(e);
		}

	}

	private UserNotification buildUserNotification(String user, SendNoficationReqModel reqModel)
			throws JsonProcessingException {
		return UserNotification.builder().message(reqModel.getMessage()).url(reqModel.getUrl())
				.notifyId(reqModel.getNotifyId()).title(reqModel.getTitle()).messageType(reqModel.getMessageType())
				.userId(user).userType(reqModel.getUserType()).icon(reqModel.getIcon()).validity(reqModel.getValidity())
				.orderRecommendation(new ObjectMapper().writeValueAsString(reqModel.getOrderRecommendation()))
				.createdBy(AppConstants.ADMIN_USER).updatedBy(AppConstants.ADMIN_USER).activeStatus(true).build();
	}

	@Override
	public List<UserNotification> getNotificationList(String userId) {
		return userNotificationRepo.getNotificationForUser(userId);
	}

}
