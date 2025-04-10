package in.codifi.basket.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchCallModelRequest {
	private String clientId;
	private String researchCall;
	private long basketId;
	private String expiryDate;
	private String status;
	private String source;
	private int sendPushNotification;
	private String PushNotificationTitle;
}
