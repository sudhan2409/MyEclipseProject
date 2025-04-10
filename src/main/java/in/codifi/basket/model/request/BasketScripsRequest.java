package in.codifi.basket.model.request;

import java.util.Date;
import java.util.List;

import in.codifi.basket.ws.model.ScripsModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketScripsRequest {
	private String basketName;
	private List<String> userId;
	private List<String> channels;
	private Date expiryDate;
	private String title;
	private String category;
	private String subCategory;
	private String shortDescription;
	private String longDescription;
	private String source;
	private String tags;
	private int sendpushNotification;
	private String pushNotificationTitle;
	private String status;
	private String speclizationTag;
	private List<ScripsModel> scrips;

}