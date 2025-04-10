package in.codifi.basket.ws.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScripsModel {
	private String remarks;
	private String exchange;
	private String token;
	private String tags;
	private String qty;
	private String price;
	private String product;
	private String tradingSymbol;
	private String transType;
	private String priceType;
	private String orderType;
	private String retention;
//	private String status;
	private String source;
	private String target;
	private String stopLoss;
	private String triggerPrice;
	private long researchCallId;
	private String expiryDate;
	private String weekTag;
//	private String speclizationTag;
	private String formattedInsName;
}