package in.codifi.basket.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetrieveBasketModel {

	private long basketId;

	private String basketName;

	private String isExecuted;

	private String createdOn;

	private long scripCount;

}
