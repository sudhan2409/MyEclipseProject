package com.sas.dto;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author GOWRI SANKAR R
 *
 */
@Getter
@Setter
public class CustomerDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String userId;
	private String accountId;
	private String password;
	private String newPassword;
	private String oldPassword;
	private String userSessionID;
	private String userToken;
	private PublicKey publicKey4;
	private String stringPkey4;
	private PublicKey publicKey3;
	private String stringPkey3;
	private PrivateKey privatekey2;
	private String stringPK2;
	private String tomcatcount;
	private String stat;
	private String tdata;
	private String sQuestions;
	private String Emsg;
	private String sCount;
	private String sIndex;
	private String ques_Ans;
	private String answer1;
	private String answer2;
	private String sUserToken;
	private String sPasswordReset;
	private String message;
	private String exch;
	private String symbol;
	private String mwName;
	private String email;
	private String pan;
	private String list;
	private String preLogin;
	private DefaultLoginDTO userSettingDto;
	private boolean is_mob;
	private String dob;
	private String scripList;
	private JSONObject validAnsResponse;
	private String webSocketID;
	private int authorizationStatus;
	private long expiryDate;
	private String userApiKey;
	private int vendorId;
	private String vendorApiKey;
	private String vendorSecret;
	private String deviceId;
	private String loginType;
	private String callBackUrl;
	private String loginMode;
	private String vendor;
	private String authCode;
	private String version;
	private boolean isV2;
	private String checkSum;
	private String fcmToken;
	private String imei;

}
