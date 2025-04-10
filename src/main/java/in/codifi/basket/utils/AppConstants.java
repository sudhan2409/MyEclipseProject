package in.codifi.basket.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class AppConstants {

	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String AUTHORIZATION = "Authorization";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	public static final String PUT_METHOD = "PUT";
	public static final String DELETE_METHOD = "DELETE";
	public static final String UTF_8 = "utf-8";
	public static final String ACCEPT = "Accept";

	public static final String FAILED_STATUS = "Failed";
	public static final String STATUS_OK = "Ok";
	public static final String STATUS_NOT_OK = "Not ok";
	public static final String SUCCESS_STATUS = "Success";
	public static final String FAILED_CODE = "400";
	public static final String SUCCESS_CODE = "200";

	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String INVALID_USER_SESSION = "Invalid user session";
	public static final String NO_RECORDS_FOUND = "No records found";
	public static final String BASKET_NAME_ALREADY_EXIST = "Basket name already exist";
	public static final String INVALID_BASKET = "Invalid basket";
	public static final String BASKET_EXECUTED = "Basket exeuted successfully";
	public static final String RECORD_DELETED = "Record Deleted";
	public static final String DELETE_FAILED = "Failed to deleted";

	public static final List<JSONObject> EMPTY_ARRAY = new ArrayList<>();

	public static final String HAZEL_KEY_REST_SESSION = "_REST_SESSION";

	public static final String REST_STATUS_NOT_OK = "not_ok";
	public static final String REST_STATUS_OK = "Ok";
	public static final String REST_NO_DATA = "no data";
	public static final String NO_RECORD_FOUND = "No records are found";

	public static final String MODULE_BASKET = "Basket";

	public static final String BASKET_LIMIT_EXIST = "Maximum limit reached: Only 25 baskets allowed";
	public static final String REACHED_MAX_SCRIP_LIMIT = "Basket reached the maximum limits";

	/* TR constans */
	public static final String JSESSONID = "jsessionid";

	public static final String AUTHORIZATION_HEADER = "JNRpwPQIDMoXywUjLwq9CQjwTtl1Y9pBh2aHz38yGyk-WGQdOBHFzD4VHeB3aZ1kTS";
	public static final String VALID_VENDOR = "valid vendor";
	public static final String MORE_THAN_MAXIMUM_SIZE = "Basket contains more than 20 scrips";
	public static final String NOT_AUTHORIZED_BY_ADMIN = "Your not authorized by admin";
	public static final String NOT_A_VENDOR = "Your not a vendor";

	public static final String ALL = "all";
	public static final String ADMIN_USER = "Admin";

	public static final String NSE = "NSE";
	public static final String BSE = "BSE";
	public static final String NFO = "NFO";
	public static final String CDS = "CDS";
	public static final String INVALID_EXCH = "Invalid Exchange";
	public static final int MAX_THREAD_SIZE = 250;

	public static final String NO_DATA_FOUND = "No data found";
	public static final String NULL_POINTER_EXCEPTION = "Null pointer exception occurred";
	public static final String DATABASE_ERROR = "Database error occurred";
	public static final String GENERAL_ERROR = "An error occurred";

}
