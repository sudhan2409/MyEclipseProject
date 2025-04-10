package in.codifi.basket.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import in.codifi.basket.config.ApplicationProperties;
import in.codifi.basket.config.FirebaseConfig;
import in.codifi.basket.model.request.SendNoficationReqModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PushNoficationUtils {

	@Inject
	ApplicationProperties props;
	@Inject
	FirebaseConfig firebaseConfig;

	/**
	 * Method to send recommendation message
	 * 
	 * @author Gowthaman
	 * @param deviceIds
	 * @param reqModel
	 */
	@SuppressWarnings("unchecked")
	public void sendNofification(List<String> deviceIds, SendNoficationReqModel reqModel, long basketId) {
		try {
			System.out.println(props.getFcmApiKey());
			URL url = new URL(props.getFcmBaseUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + props.getFcmApiKey());
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject requestNew = new JSONObject();
			requestNew.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
			requestNew.put("collapse_key", "1234");

			JSONObject androidNew = new JSONObject();
			JSONObject notificationAndroidNew = new JSONObject();
			notificationAndroidNew.put("channel_id", "custom_notification_codifi");
			androidNew.put("notification", notificationAndroidNew);
			requestNew.put("android", androidNew);

			JSONObject noramlNotification = new JSONObject();
			noramlNotification.put("android_channel_id", "custom_notification_codifi");
			noramlNotification.put("channel_id", "custom_notification_codifi");
			noramlNotification.put("title_color", "#2a6d57");
			noramlNotification.put("title", reqModel.getTitle());
			noramlNotification.put("body", reqModel.getMessage());
			requestNew.put("notification", noramlNotification);
			requestNew.put("registration_ids", deviceIds);

			JSONObject data = new JSONObject();

			if (reqModel.getMessageType().equalsIgnoreCase("Info")) {
				data.put("type", "Info");
			} else if (reqModel.getMessageType().equalsIgnoreCase("url")) {
				data.put("type", "ext_url");
				data.put("url", reqModel.getUrl());
			} else if (reqModel.getMessageType().equalsIgnoreCase("Order")) {
				data.put("type", "Order");
				data.put("orderRecommentation", reqModel.getOrderRecommendation());
			} else if (reqModel.getMessageType().equalsIgnoreCase("Basket")) {
				data.put("type", "Basket");
				data.put("basketId", basketId);
				data.put("notificationId", reqModel.getNotifyId());
			}

			requestNew.put("data", data);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(requestNew.toString());
			wr.flush();
			wr.close();
			int responseCode = conn.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to send push notification for android mobile
	 * 
	 * @author Dinesh Kumar
	 * @param deviceIds
	 * @param reqModel
	 */
	public void sendAndroidMulticastNotification(String title, String message, String messageType, String url,
			List<String> deviceIds, String userId) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {

					verifyAndLoadFireBaseConfig("android");

					FirebaseApp androidApp = FirebaseApp.getInstance("android");

					Map<String, String> data = new HashMap<>();
					if (StringUtil.isNotNullOrEmpty(messageType)) {
						if (messageType.equalsIgnoreCase("url")) {
							data.put("type", "ext_url");
							data.put("url", url);
						} else {
							data.put("type", messageType);
						}
					} else {
						data.put("type", "Info");
					}

					// Create Android-specific notification
					AndroidNotification androidNotification = AndroidNotification.builder()
							.setClickAction("FLUTTER_NOTIFICATION_CLICK").setColor("#2a6d57")
							.setChannelId("abm_notifications").build();

					// Create MulticastMessage to send to multiple devices
					MulticastMessage reqMessage = MulticastMessage.builder()
							.setNotification(Notification.builder().setTitle(title).setBody(message).build())
							.setAndroidConfig(AndroidConfig.builder().setCollapseKey("1234")
									.setNotification(androidNotification).build())
							.addAllTokens(deviceIds).putAllData(data).build();

					// Send the multicast message
					BatchResponse response = FirebaseMessaging.getInstance(androidApp).sendEachForMulticast(reqMessage);

				} catch (Exception e) {
					Log.error("Error sending Android FCM message: " + e);
				} finally {
					pool.shutdown();
				}
			}
		});
	}

	/**
	 * Method to load firebase config idf not exist
	 * 
	 * @author Dinesh Kumar
	 * @param name
	 */
	private void verifyAndLoadFireBaseConfig(String name) {

		List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
		FirebaseApp firebaseapp = null;
		for (FirebaseApp app : firebaseApps) {
			if (app.getName().equals(name)) {
				firebaseapp = app;
				break;
			}
		}
		if (firebaseapp == null) {
			firebaseConfig.initializeFirebaseApp(name);
		}
	}

	/**
	 * Method to send push notification for IOS mobile
	 * 
	 * @author Dinesh Kumar
	 * @param deviceIds
	 * @param reqModel
	 */
	public void sendIosMulticastNotification(String title, String message, String messageType, String url,
			List<String> deviceIds, String userId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {

					verifyAndLoadFireBaseConfig("ios");
					FirebaseApp iosApp = FirebaseApp.getInstance("ios");

					Map<String, Object> data = null;
					if (StringUtil.isNotNullOrEmpty(messageType)) {
						if (messageType.equalsIgnoreCase("url")) {
							data = Map.of("type", "ext_url", "url", url);
						} else {
							data = Map.of("type", "messageType");
						}
					} else {
						data = Map.of("type", "Info");
					}

					/** Convert the nested data to a JSON string **/
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonData = objectMapper.writeValueAsString(data);

					/** Create MulticastMessage builder for iOS devices **/
//					MulticastMessage reqMessage = MulticastMessage.builder()
//							// .setNotification(Notification.builder().setTitle(title).setBody(message).build())
//							.putData("title", title) /** Data for the title **/
//							.putData("body", message) /** Data for the body **/
//							.putData("keysandvalues", jsonData)
//							.setApnsConfig(ApnsConfig
//									.builder().setAps(
//											Aps.builder().setCategory("FLUTTER_NOTIFICATION_CLICK")
//													.setContentAvailable(true) /** Enable background processing **/
//													.setAlert(ApsAlert.builder().setTitle(title) /** Display title **/
//															.setBody(message) /** Display notification body **/
//															.build())
//													.build())
//									.putHeader("apns-collapse-id", "1234") /** Optional: collapse ID **/
//									.build())
//							.addAllTokens(deviceIds).build();

					MulticastMessage.Builder messageBuilder = MulticastMessage.builder();

					messageBuilder
							.setApnsConfig(ApnsConfig
									.builder().setAps(
											Aps.builder().setCategory("FLUTTER_NOTIFICATION_CLICK")
													.setContentAvailable(true) /** Enable background processing **/
													.setAlert(ApsAlert.builder().setTitle(title) /** Display title **/
															.setBody(message) /** Display notification body **/
															.build())
													.build())
									.putHeader("apns-collapse-id", "1234") /** Optional: collapse ID **/
									.build())
							.addAllTokens(deviceIds);

					if (StringUtil.isNotNullOrEmpty(messageType)) {
						if (messageType.equalsIgnoreCase("url")) {
							messageBuilder.putData("type", "ext_url");
							messageBuilder.putData("url", url);
						} else {
							messageBuilder.putData("type", "messageType");
						}
					} else {
						messageBuilder.putData("type", "Info");
					}

					MulticastMessage reqMessage = messageBuilder.build();

					// Send the multicast message
					BatchResponse response = FirebaseMessaging.getInstance(iosApp).sendEachForMulticast(reqMessage);
				} catch (Exception e) {
					Log.error("Error sending iOS FCM message: " + e);
				} finally {
					pool.shutdown();
				}
			}
		});
	}

}
