package in.codifi.basket.ws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sas.dto.CustomerDTO;
import com.tr.nest.crypto.CryptoRSA;

import in.codifi.basket.config.HazelcastConfig;
import in.codifi.basket.config.RestServiceProperties;
import in.codifi.basket.entity.logs.RestAccessLogModel;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.model.response.SpanMarginResp;
import in.codifi.basket.repository.AccessLogManager;
import in.codifi.basket.utils.AppConstants;
import in.codifi.basket.utils.CodifiUtil;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.utils.StringUtil;
import in.codifi.basket.ws.model.BasketMarginRestReqModel;
import in.codifi.basket.ws.model.BasketMarginRestRespModel;
import in.codifi.basket.ws.model.SpanMarginRestResp;
import io.quarkus.logging.Log;

@ApplicationScoped
public class SpanMarginRestService {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	RestServiceProperties props;
	@Inject
	AccessLogManager accessLogManager;

	CryptoRSA nRSA = new CryptoRSA();

	/**
	 * Method to connect the API
	 * 
	 * @author SOWMIYA
	 * @param baseUrl
	 * @return
	 */
	public String getSpanMargin(String request) {
		Log.info("span margin Request" + request);
		String response = AppConstants.FAILED_STATUS;
		try {
			URL url = new URL(props.getSpanMarginUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.TEXT_PLAIN);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = request.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			BufferedReader bufferedReader;
			String output = null;
			if (conn.getResponseCode() == 401) {
				Log.error("Unauthorized error in span margin api");
			} else if (conn.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				Log.info("span margin response" + output);
				if (StringUtil.isNotNullOrEmpty(output)) {

					ObjectMapper mapper = new ObjectMapper();
					SpanMarginRestResp spanResponseModel = mapper.readValue(output, SpanMarginRestResp.class);
					if (StringUtil.isNotNullOrEmpty(spanResponseModel.getStat())
							&& spanResponseModel.getStat().equalsIgnoreCase(AppConstants.REST_STATUS_OK)) {

						double spanTrade = 0;
						double expoTrade = 0;
						double totalSpan = 0;
						if (StringUtil.isNotNullOrEmpty(spanResponseModel.getSpan_trade())) {
							spanTrade = Double.valueOf(spanResponseModel.getSpan_trade());
						}
						if (StringUtil.isNotNullOrEmpty(spanResponseModel.getExpo_trade())) {
							expoTrade = Double.valueOf(spanResponseModel.getExpo_trade());
						}
						totalSpan = spanTrade + expoTrade;
						return String.valueOf(totalSpan);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;

	}

	/**
	 * method to get basket margin
	 * 
	 * @author SowmiyaThangaraj
	 * @param request
	 * @return
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 */
	public RestResponse<GenericResponse> getBasketMargin(List<BasketMarginRestReqModel> request, String userId)
			throws JsonProcessingException, UnsupportedEncodingException {
		Log.info("basket margin Request" + request);
		RestAccessLogModel accessLogModel = new RestAccessLogModel();
		ObjectMapper mapper = new ObjectMapper();
		JSONArray reqModel = new JSONArray();
		BasketMarginRestRespModel basketResponseModel = new BasketMarginRestRespModel();

		try {
			CodifiUtil.trustedManagement();
			String baseUrl = props.getBasketMarginUrl();
			CustomerDTO customerKeyDto = HazelcastConfig.getInstance().getUserKeyMap().get(userId);
//			String json = mapper.writeValueAsString(reqModel);
			String json = reqModel.toString();
			for (BasketMarginRestReqModel entity : request) {
				JSONObject object = new JSONObject();
				object.put("exch", entity.getExch());
				object.put("symbol", entity.getSymbol());
				object.put("netQty", entity.getQty());
				object.put("buyQty", "");
				object.put("sellQty", "");
				reqModel.add(object);
			}
			System.out.println("json---------------------------------------" + json);
			System.out.println("reqModel---------------------------------------" + reqModel);
			String uRL = baseUrl + "?" + AppConstants.JSESSONID + "=." + customerKeyDto.getTomcatcount() + "&jData="
					+ URLEncoder.encode(reqModel.toString(), "UTF-8") + "&jKey=" + customerKeyDto.getStringPkey4();
			Log.info("Security info BaseUrl - " + baseUrl);
			System.out.println("uRL---------------------------------------" + uRL);
			accessLogModel.setMethod("getBasketMargin");
			accessLogModel.setModule(AppConstants.MODULE_BASKET);
			accessLogModel.setUrl(props.getBasketMarginUrl());
			accessLogModel.setReqBody(json);
			accessLogModel.setUserId(userId);
			accessLogModel.setInTime(new Timestamp(new Date().getTime()));

			URL url = new URL(uRL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(AppConstants.POST_METHOD);
			conn.setRequestProperty(AppConstants.ACCEPT, AppConstants.TEXT_PLAIN);
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = json.getBytes(AppConstants.UTF_8);
				os.write(input, 0, input.length);
			}
			int responseCode = conn.getResponseCode();
			accessLogModel.setOutTime(new Timestamp(new Date().getTime()));
			BufferedReader bufferedReader;
			String output = null;
			if (responseCode == 401) {
				Log.error("Unauthorized error in basket margin api");
				accessLogModel.setResBody("Unauthorized");
				insertRestAccessLogs(accessLogModel);
				return prepareResponse.prepareUnauthorizedResponse();
			} else if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				output = bufferedReader.readLine();
				if (StringUtil.isNotNullOrEmpty(output)) {
					accessLogModel.setResBody(output);
					insertRestAccessLogs(accessLogModel);
					basketResponseModel = mapper.readValue(output, BasketMarginRestRespModel.class);
					if (StringUtil.isNotNullOrEmpty(basketResponseModel.getStat())
							&& basketResponseModel.getStat().equalsIgnoreCase(AppConstants.REST_STATUS_OK)) {
						SpanMarginResp resp = new SpanMarginResp();
						resp.setSpan(basketResponseModel.getSpanRequirement());
						return prepareResponse.prepareSuccessResponseObject(resp);
					} else {
						return prepareResponse.prepareFailedResponse(basketResponseModel.getEmsg());
					}

				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				output = bufferedReader.readLine();
				accessLogModel.setResBody(output);
				insertRestAccessLogs(accessLogModel);
				if (StringUtil.isNotNullOrEmpty(output)) {
					basketResponseModel = mapper.readValue(output, BasketMarginRestRespModel.class);
					if (StringUtil.isNotNullOrEmpty(basketResponseModel.getEmsg()))
						System.out.println("Error Connection in basket margin api. Response code -"
								+ basketResponseModel.getEmsg());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * 
	 * Method to insert rest service access logs
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param accessLogModel
	 */
	public void insertRestAccessLogs(RestAccessLogModel accessLogModel) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					accessLogManager.insertRestAccessLog(accessLogModel);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.shutdown();
				}
			}
		});
	}

}
