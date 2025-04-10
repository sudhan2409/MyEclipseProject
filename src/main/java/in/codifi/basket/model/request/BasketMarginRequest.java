package in.codifi.basket.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketMarginRequest {

	private String exchange;
	private String symbol;
	private String qty;
	private String token;
	private String price;
	private String transType;
}
