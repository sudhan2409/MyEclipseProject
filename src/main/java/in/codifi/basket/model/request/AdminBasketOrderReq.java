package in.codifi.basket.model.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBasketOrderReq {

	private String apiKey;
	private String basketName;
	private List<String> userId;
	private int basketId;
	private String description;
	private Date expiryDate;
	private int pushNotification;
	private String message;
	private String title;
	private List<ScripRequestModel> scrips;
	List<Long> scripsId = new ArrayList<Long>();
	private String Category;
	private String subCategory;

}
