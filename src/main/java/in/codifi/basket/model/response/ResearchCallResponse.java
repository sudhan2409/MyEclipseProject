package in.codifi.basket.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchCallResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long basketId;

	private String lotSize;

	private String sortOrder;

	private String exchange;

	private String token;

	private String tradingSymbol;

	private String qty;

	private String price;

	private Date expiry;

	private String product;

	private String transType;

	private String priceType;

	private String orderType;

	private String ret;

	private String triggerPrice;

	private String disClosedQty;

	private String speclizationTag;

	private String mktProtection;

	private String target;

	private int status;

	private String stopLoss;

	private String trailingStopLoss;

	private String formattedInsName;

	private String weekTag;

	private String validityDays;

	private String expiryDate;

	private String userSegment;

}
