package in.codifi.basket.ws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketMarginRestReqModel {

	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("exch")
	private String exch;
	@JsonProperty("qty")
	private String qty;
	@JsonProperty("basketlists")
	private List<BasketListModel> basketlists;

}
