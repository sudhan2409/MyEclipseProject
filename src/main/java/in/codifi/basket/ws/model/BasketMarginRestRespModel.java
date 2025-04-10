package in.codifi.basket.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketMarginRestRespModel {

	@JsonProperty("totalRequirement")
	private String totalRequirement;
	@JsonProperty("stat")
	private String stat;
	@JsonProperty("emsg")
	private String emsg;
	@JsonProperty("spreadBenefit")
	private String spreadBenefit;
	@JsonProperty("exposureMarginPrst")
	private String exposureMarginPrst;
	@JsonProperty("spanRequirement")
	private String spanRequirement;

}
