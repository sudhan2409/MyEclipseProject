package in.codifi.basket.ws.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchCallModelResponse {

	private int id;
	private int sortOrder;
	private String basketName;
	private String userId;
	private Date expiryDate;
	private String createdOn;
	private String createdBy;
	private String speclizationTag;
	private String status;
	private String shortDescription;
	private String longDescription;
	private String remarks;
	private String category;
	private String channels;
	private String subCategory;
	private String tags;
	private String researchcall;
	private String vendorCode;
	private String isExecuted;
	private String isVendorBasket;
	private String source;
	private String activeStatus;
	private String sendPushNotification;
	private String PushNotificationTitle;

}