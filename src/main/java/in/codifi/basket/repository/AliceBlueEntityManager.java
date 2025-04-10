package in.codifi.basket.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.basket.entity.primary.VendorAppEntity;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AliceBlueEntityManager {

//	@Named("aliceblue")
	@Inject
	DataSource dateSource;

	/**
	 * method to validate tpp authorized vendor
	 * 
	 * @author SowmiyaThangaraj
	 * @param apiKey
	 * @return
	 */
	public VendorAppEntity findByApiKey(String apiKey) {
		VendorAppEntity vendor = new VendorAppEntity();
		PreparedStatement pStmt = null;
		Connection conn = null;
		ResultSet rSet = null;
		try {
			conn = dateSource.getConnection();
			String query = "SELECT app_name, api_key, api_secret, tpp_authorization, client_id, contact_name, mobile_no, email, type, "
					+ "icon_url, redirect_url, postback_url, description, authorization_status, is_accepted, rejected_reson FROM tbl_vendor_app where api_key = ? ";
			pStmt = conn.prepareStatement(query);
			pStmt.setString(1, apiKey);
			rSet = pStmt.executeQuery();
			if (rSet.next()) {
				vendor.setAppName(rSet.getString("app_name"));
				vendor.setApiKey(rSet.getString("api_key"));
				vendor.setApiSecret(rSet.getString("api_secret"));
				vendor.setTppAuthorization(rSet.getInt("tpp_authorization"));
				System.out.println("tpp_authorization----------------------" + rSet.getInt("tpp_authorization"));
				vendor.setClient_id(rSet.getString("client_id"));
				vendor.setContact_name(rSet.getString("contact_name"));
				vendor.setMobile_no(rSet.getString("mobile_no"));
				vendor.setEmail(rSet.getString("email"));
				vendor.setType(rSet.getString("type"));
				vendor.setIcon_url(rSet.getString("icon_url"));
				vendor.setRedirect_url(rSet.getString("redirect_url"));
				vendor.setPostback_url(rSet.getString("postback_url"));
				vendor.setDescription(rSet.getString("description"));
				vendor.setAuthorization_status(rSet.getInt("authorization_status"));
				vendor.setIs_accepted(rSet.getInt("is_accepted"));
				vendor.setRejected_reson(rSet.getString("rejected_reson"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("validateTPPAuthorizedVendor - ", e);
		}
		return vendor;
	}

}
