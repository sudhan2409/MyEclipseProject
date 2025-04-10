package in.codifi.basket.model.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchCallRequest {
	private long id;
	private String clientId;
	private String basketId;
	private String activeStatus;
	private String researchCall;
	private Long researchCallId;
	private String status;
	private String category;
	private String subCategory;
	private List<String> tags;
	private String fromDate;
	private String toDate;
	private String remarks;
}
