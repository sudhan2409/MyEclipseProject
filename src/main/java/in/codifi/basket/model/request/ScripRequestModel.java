package in.codifi.basket.model.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScripRequestModel {

	private long id;

	private String sortOrder;

	private String exchange;

	private String token;

	private String tradingSymbol;

	private String qty;

	private String price;

	private String product;

	private String transType;

	private String priceType;

	private String orderType;

	private String ret;

	private String triggerPrice;

	private String disClosedQty;

	private String mktProtection;

	private String target;

	private String stopLoss;

	private String trailingStopLoss;

	private String speclizationTag;

	private String createdBy;

	private String source;

	private String formattedInsName;

	private String weekTag;

	private Date expiry;

	private String validityDays;

	private String expiryDate;

	private String lotSize;

	private String remarks;

	private String tags;
	
	private String userSegment;
	
	

}
