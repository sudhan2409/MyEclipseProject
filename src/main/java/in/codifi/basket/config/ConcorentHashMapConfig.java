package in.codifi.basket.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import in.codifi.basket.entity.primary.DeviceMappingEntity;
import in.codifi.basket.model.response.ResearchCallResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class ConcorentHashMapConfig {

	private Map<String, List<String>> clientList = new ConcurrentHashMap<>();
	private Map<String, List<String>> researchCallScrip = new ConcurrentHashMap<>();

	private Map<String, List<DeviceMappingEntity>> deviceMappingDetails = new ConcurrentHashMap<>();

	private Map<String, Map<String, Map<String, List<ResearchCallResponse>>>> researchcall = new ConcurrentHashMap<>();
	private Map<Long, Map<String, Map<String, List<Map<String, Object>>>>> basketid = new ConcurrentHashMap<>();
}
