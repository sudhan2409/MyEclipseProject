package in.codifi.basket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import in.codifi.basket.model.request.ResearchCallRequest;
import in.codifi.basket.ws.model.ResearchCallModelResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ResearchCallDAO {
	@Inject
	DataSource dataSource;

	/**
	 * 
	 * Method to fetch basket details
	 *
	 * @author gokul raj
	 *
	 * @param pReq
	 * @param pClientId
	 * @param all
	 * @return
	 */
	public List<ResearchCallModelResponse> getBasketData(ResearchCallRequest pReq, String pClientId, String all) {
		List<ResearchCallModelResponse> list = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT id, basket_name, user_id, expiry_date, "
				+ "category, subcategory, tags, shortDescription, longDescription, is_executed, "
				+ "is_vendor_basket, researchcall, vendor_code, remarks,pushnotification_title,send_pushnotification,source,status,created_on,speclization_tag "
				+ "FROM abml.tbl_researchcall_master s");

		boolean hasFilters = false;

		// Check if the pClientId is not null and apply the condition
		if (pClientId != null && !pClientId.isEmpty()) {
			query.append(" WHERE s.user_id in (?,'ALL')");
			hasFilters = true;
		}

		if (pReq != null) {
			// Apply filters from ResearchCallRequest if it is not null
			if (pReq.getCategory() != null && !pReq.getCategory().isEmpty()) {
				query.append(hasFilters ? " AND" : " WHERE").append(" s.category = ?");
				hasFilters = true;
			}
			if (pReq.getSubCategory() != null && !pReq.getSubCategory().isEmpty()) {
				query.append(hasFilters ? " AND" : " WHERE").append(" s.subcategory = ?");
				hasFilters = true;
			}
			if (pReq.getTags() != null && !pReq.getTags().isEmpty()) {
				// Start with "WHERE" or "AND" based on previous filters
				if (hasFilters) {
					query.append(" AND");
				} else {
					query.append(" WHERE");
				}

				// Add the tags filter condition
				query.append(" s.tags IN (");

				// Add a "?" for each tag and separate them with commas
				for (int i = 0; i < pReq.getTags().size(); i++) {
					query.append("?");
					if (i < pReq.getTags().size() - 1) {
						query.append(", "); // Add a comma between placeholders
					}
				}

				query.append(")");

				// Mark that a filter has been added
				hasFilters = true;
			}

			// Handle the date range filter logic
			String fromDate = pReq.getFromDate();
			String toDate = pReq.getToDate();

			if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
				// Case 1: Both fromDate and toDate are provided, apply the BETWEEN condition
				query.append(hasFilters ? " AND" : " WHERE").append(" s.expiry_date BETWEEN ? AND ?");
				hasFilters = true;
			} else {
				// Case 2: Only fromDate is provided, set toDate as the same as fromDate
				if (fromDate != null && !fromDate.isEmpty()) {
					query.append(hasFilters ? " AND" : " WHERE").append(" s.expiry_date = ?");
					hasFilters = true;
					toDate = fromDate; // Set toDate to the same as fromDate
				}

				// Case 3: Neither fromDate nor toDate is provided, use the last 30 days
				if (fromDate == null || fromDate.isEmpty()) {
					query.append(hasFilters ? " AND" : " WHERE")
							.append(" s.expiry_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)");
					hasFilters = true;
				}
				if (toDate == null || toDate.isEmpty()) {
					query.append(hasFilters ? " AND" : " WHERE").append(" s.expiry_date <= CURDATE()");
					hasFilters = true;
				}
			}

			// Add filter for status if it is provided in the request
			if (pReq.getStatus() != null && !pReq.getStatus().isEmpty()) {
				query.append(hasFilters ? " AND" : " WHERE").append(" s.status = ?");
				hasFilters = true;
			}

		}

		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet resultSet = null;

		try {
			// Establish the connection
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement(query.toString());

			int index = 1;

			// Set pClientId parameter
			if (pClientId != null && !pClientId.isEmpty()) {
				pStmt.setString(index++, pClientId);
			}

			// Set parameters for pReq filters if it is not null
			if (pReq != null) {
				if (pReq.getCategory() != null && !pReq.getCategory().isEmpty()) {
					pStmt.setString(index++, pReq.getCategory());
				}
				if (pReq.getSubCategory() != null && !pReq.getSubCategory().isEmpty()) {
					pStmt.setString(index++, pReq.getSubCategory());
				}
				if (pReq.getTags() != null && !pReq.getTags().isEmpty()) {
					for (String tag : pReq.getTags()) {
						pStmt.setString(index++, tag);
					}
				}

				// Set fromDate and toDate parameters if applicable
				if (pReq.getFromDate() != null && !pReq.getFromDate().isEmpty()) {
					pStmt.setString(index++, pReq.getFromDate());
				}
				if (pReq.getToDate() != null && !pReq.getToDate().isEmpty()) { // We may have set toDate to fromDate
					pStmt.setString(index++, pReq.getToDate());
				}

				// Set status parameter if it's provided
				if (pReq.getStatus() != null && !pReq.getStatus().isEmpty()) {
					pStmt.setString(index++, pReq.getStatus());
				}
			}

			resultSet = pStmt.executeQuery();

			while (resultSet.next()) {
				ResearchCallModelResponse response = new ResearchCallModelResponse();
				response.setId(resultSet.getInt("id"));
				response.setBasketName(resultSet.getString("basket_name"));
				response.setUserId(resultSet.getString("user_id"));
				response.setExpiryDate(resultSet.getDate("expiry_date"));
				response.setCategory(resultSet.getString("category"));
				response.setSubCategory(resultSet.getString("subcategory"));
				response.setTags(resultSet.getString("tags"));
				response.setShortDescription(resultSet.getString("shortDescription"));
				response.setLongDescription(resultSet.getString("longDescription"));
				response.setIsExecuted(resultSet.getString("is_executed"));
				response.setIsVendorBasket(resultSet.getString("is_vendor_basket"));
				response.setResearchcall(resultSet.getString("researchcall"));
				response.setVendorCode(resultSet.getString("vendor_code"));
				response.setRemarks(resultSet.getString("remarks"));
				response.setSendPushNotification(resultSet.getString("send_pushnotification"));
				response.setPushNotificationTitle(resultSet.getString("pushnotification_title"));
				response.setSource(resultSet.getString("source"));
				response.setStatus(resultSet.getString("status"));
				response.setSpeclizationTag(resultSet.getString("speclization_tag"));
				response.setCreatedOn(resultSet.getString("created_on"));
				list.add(response);
			}

		} catch (SQLException e) {
			Log.error("Error retrieving data from table: " + e.getMessage());
		} finally {
			// Close resources explicitly in the finally block to ensure they are closed
			// even in case of an exception
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				Log.error("Error closing resources: " + e.getMessage());
			}
		}

		return list;
	}

//	public List<ResearchCallModelResponse> getBasketData1(ResearchCallRequest pReq, String pClientId, String ALL) {
//		List<ResearchCallModelResponse> list = new ArrayList<>();
//		StringBuilder query = new StringBuilder(
//				"SELECT id, basket_name, legs_count, user_id, scrip_count, expiry_date, "
//						+ "category, subcategory, tags, shortDescription, longDescription, is_executed, "
//						+ "is_vendor_basket, researchcall, vendor_code, remarks, send_pushnotification, "
//						+ "pushnotification_title, source, status, created_on, publish, speclization_tag "
//						+ "FROM abml.tbl_researchcall_master s");
//
//		boolean hasFilters = false;
//
//		// Handling the clientId condition using ternary operator
//		query.append(pClientId == null ? " WHERE s.user_id = 'ALL'" // If pClientId is null, use 'ALL'
//				: " WHERE s.user_id = ?"); // If pClientId is not null, use client_id
//		hasFilters = true;
//
//		if (pReq != null) {
//			// Category filter
//			if (pReq.getCategory() != null && !pReq.getCategory().isEmpty()) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.category = ?");
//				hasFilters = true;
//			}
//
//			// SubCategory filter
//			if (pReq.getSubCategory() != null && !pReq.getSubCategory().isEmpty()) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.subcategory = ?");
//				hasFilters = true;
//			}
//
//			// Tags filter
//			if (pReq.getTags() != null && !pReq.getTags().isEmpty()) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.tags IN (");
//				for (int i = 0; i < pReq.getTags().size(); i++) {
//					query.append("?");
//					if (i < pReq.getTags().size() - 1) {
//						query.append(", ");
//					}
//				}
//				query.append(")");
//				hasFilters = true;
//			}
//
//			// Date range filter
//			String fromDate = pReq.getFromDate();
//			String toDate = pReq.getToDate();
//			if (fromDate != null && toDate != null) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.created_on BETWEEN ? AND ?");
//				hasFilters = true;
//			} else if (fromDate != null) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.created_on = ?");
//				hasFilters = true;
//			} else if (toDate != null) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.created_on <= ?");
//				hasFilters = true;
//			} else {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.created_on >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
//						.append(" AND s.created_on <= NOW()");
//				hasFilters = true;
//			}
//
//			// Status filter
//			if (pReq.getStatus() != null && !pReq.getStatus().isEmpty()) {
//				query.append(hasFilters ? " AND" : " WHERE").append(" s.status = ?");
//				hasFilters = true;
//			}
//		}
//
//		// Now execute the query with the appropriate parameters
//		Connection conn = null;
//		PreparedStatement pStmt = null;
//		ResultSet resultSet = null;
//
//		try {
//			conn = dataSource.getConnection(); // Ensure `dataSource` is properly configured
//			pStmt = conn.prepareStatement(query.toString());
//
//			int index = 1;
//
//			// Set the parameters for the prepared statement
//			if (pClientId == null) {
//				pStmt.setString(index++, "ALL"); // Set the 'ALL' value if pClientId is null
//			} else {
//				pStmt.setString(index++, pClientId); // Set the pClientId
//			}
//
//			if (pReq != null) {
//				if (pReq.getCategory() != null && !pReq.getCategory().isEmpty()) {
//					pStmt.setString(index++, pReq.getCategory());
//				}
//
//				if (pReq.getSubCategory() != null && !pReq.getSubCategory().isEmpty()) {
//					pStmt.setString(index++, pReq.getSubCategory());
//				}
//
//				if (pReq.getTags() != null && !pReq.getTags().isEmpty()) {
//					for (String tag : pReq.getTags()) {
//						pStmt.setString(index++, tag);
//					}
//				}
//
//				if (pReq.getFromDate() != null) {
//					pStmt.setString(index++, pReq.getFromDate());
//				}
//
//				if (pReq.getToDate() != null) {
//					pStmt.setString(index++, pReq.getToDate());
//				}
//
//				if (pReq.getStatus() != null && !pReq.getStatus().isEmpty()) {
//					pStmt.setString(index++, pReq.getStatus());
//				}
//			}
//
//			resultSet = pStmt.executeQuery();
//
//			while (resultSet.next()) {
//				ResearchCallModelResponse response = new ResearchCallModelResponse();
//				response.setId(resultSet.getInt("id"));
//				response.setBasketName(resultSet.getString("basket_name"));
//				response.setUserId(resultSet.getString("user_id"));
//				response.setExpiryDate(resultSet.getTimestamp("expiry_date"));
//				response.setCategory(resultSet.getString("category"));
//				response.setSubCategory(resultSet.getString("subcategory"));
//				response.setTags(resultSet.getString("tags"));
//				response.setCreatedOn(resultSet.getString("created_on"));
//				response.setShortDescription(resultSet.getString("shortDescription"));
//				response.setLongDescription(resultSet.getString("longDescription"));
//				response.setIsExecuted(resultSet.getString("is_executed"));
//				response.setIsVendorBasket(resultSet.getString("is_vendor_basket"));
//				response.setResearchcall(resultSet.getString("researchcall"));
//				response.setVendorCode(resultSet.getString("vendor_code"));
//				response.setRemarks(resultSet.getString("remarks"));
//				response.setSendPushNotification(resultSet.getString("send_pushnotification"));
//				response.setPushNotificationTitle(resultSet.getString("pushnotification_title"));
//				response.setSource(resultSet.getString("source"));
//				response.setStatus(resultSet.getString("status"));
//				response.setSpeclizationTag(resultSet.getString("speclization_tag"));
//				list.add(response);
//			}
//		} catch (SQLException e) {
//			Log.error("Error retrieving data: " + e.getMessage());
//		} finally {
//			try {
//				if (resultSet != null)
//					resultSet.close();
//				if (pStmt != null)
//					pStmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				Log.error("Error closing resources: " + e.getMessage());
//			}
//		}
//
//		return list;
//	}

	public List<ResearchCallModelResponse> getBasketData1(String pClientId) {
		List<ResearchCallModelResponse> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet resultSet = null;

		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement("SELECT id, basket_name, legs_count, user_id, scrip_count, expiry_date,"
					+ "category, subcategory, tags, shortDescription, longDescription, is_executed,"
					+ "is_vendor_basket, researchcall, vendor_code, remarks, send_pushnotification,"
					+ "pushnotification_title, source, status, created_on, publish, speclization_tag, sort_order"
//					+ " FROM tbl_researchcall_master  where user_id in (?,'All') order by id desc");
					+ " FROM tbl_researchcall_master  where user_id in (?,'All') order by created_on desc");
			int paramPos = 1;
			pStmt.setString(paramPos++, pClientId);
			resultSet = pStmt.executeQuery();

			while (resultSet.next()) {
				ResearchCallModelResponse response = new ResearchCallModelResponse();
				response.setId(resultSet.getInt("id"));
				response.setBasketName(resultSet.getString("basket_name"));
				response.setUserId(resultSet.getString("user_id"));
				response.setExpiryDate(resultSet.getTimestamp("expiry_date"));
				response.setCategory(resultSet.getString("category"));
				response.setSubCategory(resultSet.getString("subcategory"));
				response.setTags(resultSet.getString("tags"));
				response.setCreatedOn(resultSet.getString("created_on"));
				response.setShortDescription(resultSet.getString("shortDescription"));
				response.setLongDescription(resultSet.getString("longDescription"));
				response.setIsExecuted(resultSet.getString("is_executed"));
				response.setIsVendorBasket(resultSet.getString("is_vendor_basket"));
				response.setResearchcall(resultSet.getString("researchcall"));
				response.setVendorCode(resultSet.getString("vendor_code"));
				response.setRemarks(resultSet.getString("remarks"));
				response.setSendPushNotification(resultSet.getString("send_pushnotification"));
				response.setPushNotificationTitle(resultSet.getString("pushnotification_title"));
				response.setSource(resultSet.getString("source"));
				response.setStatus(resultSet.getString("status"));
				response.setSortOrder(resultSet.getInt("sort_order"));
				response.setSpeclizationTag(resultSet.getString("speclization_tag"));
				list.add(response);
			}
		} catch (SQLException e) {
			Log.error("Error retrieving data: " + e.getMessage());
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (pStmt != null)
					pStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				Log.error("Error closing resources: " + e.getMessage());
			}
		}

		return list;
	}

//	public List<ResearchCallModelResponse> getBasketData1(List<Integer> researchCallIds) {
//		List<ResearchCallModelResponse> list = new ArrayList<>();
//		Connection conn = null;
//		PreparedStatement pStmt = null;
//		ResultSet resultSet = null;
//
//		if (researchCallIds == null || researchCallIds.isEmpty()) {
//			return list; // Return empty list if no IDs are provided
//		}
//
//		try {
//			conn = dataSource.getConnection();
//
//			// Dynamically create the IN clause for researchCallIds
//			StringBuilder query = new StringBuilder("SELECT * FROM tbl_researchcall_master " + "WHERE id IN (");
//			// Append placeholders for prepared statement
//			query.append(researchCallIds.stream().map(id -> "?").collect(Collectors.joining(",")));
//			query.append(") ORDER BY created_on DESC");
//
//			pStmt = conn.prepareStatement(query.toString());
//
//			// Set parameters for the IN clause
//			int index = 1;
//			for (Integer id : researchCallIds) {
//				pStmt.setInt(index++, id);
//			}
//
//			resultSet = pStmt.executeQuery();
//
//			while (resultSet.next()) {
//				ResearchCallModelResponse response = new ResearchCallModelResponse();
//				response.setId(resultSet.getInt("id"));
//				response.setBasketName(resultSet.getString("basket_name"));
//				response.setUserId(resultSet.getString("user_id"));
//				response.setExpiryDate(resultSet.getTimestamp("expiry_date"));
//				response.setCategory(resultSet.getString("category"));
//				response.setSubCategory(resultSet.getString("subcategory"));
//				response.setTags(resultSet.getString("tags"));
//				response.setCreatedOn(resultSet.getString("created_on"));
//				response.setShortDescription(resultSet.getString("shortDescription"));
//				response.setLongDescription(resultSet.getString("longDescription"));
//				response.setIsVendorBasket(resultSet.getString("is_vendor_basket"));
//				response.setResearchcall(resultSet.getString("researchcall"));
//				response.setVendorCode(resultSet.getString("vendor_code"));
//				response.setRemarks(resultSet.getString("remarks"));
//				response.setPushNotificationTitle(resultSet.getString("pushnotification_title"));
//				response.setSource(resultSet.getString("source"));
//				response.setStatus(resultSet.getString("status"));
//				response.setSpeclizationTag(resultSet.getString("speclization_tag"));
//
//				list.add(response);
//			}
//		} catch (SQLException e) {
//			Log.error("Error retrieving data: " + e.getMessage());
//		} finally {
//			try {
//				if (resultSet != null)
//					resultSet.close();
//				if (pStmt != null)
//					pStmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				Log.error("Error closing resources: " + e.getMessage());
//			}
//		}
//
//		return list;
//	}

	/**
	 * 
	 * Method to fetch uniq status
	 *
	 * @author gokul raj
	 *
	 * @return
	 */
	public List<String> getUniqStatus() {
		List<String> uniqueStatuses = new ArrayList<>();
		String query = "SELECT DISTINCT status FROM tbl_researchcall_master";

		try (Connection conn = dataSource.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(query);
				ResultSet rs = pStmt.executeQuery()) {

			while (rs.next()) {
				uniqueStatuses.add(rs.getString("status"));
			}

		} catch (SQLException e) {
			Log.error("Error fetching unique statuses from tbl_researchcall_master: ", e);
		}

		return uniqueStatuses;
	}

	/**
	 *
	 * Method to fetch basket details
	 *
	 * @author gokul raj
	 *
	 * @param pReq
	 * @param pClientId
	 * @param all
	 * @return
	 */
	public List<ResearchCallModelResponse> getBasketDataByResearchCallIds(List<Integer> researchCallIds) {
		List<ResearchCallModelResponse> list = new ArrayList<>();

		if (researchCallIds == null || researchCallIds.isEmpty()) {
			return list;
		}

		StringBuilder query = new StringBuilder("SELECT * FROM abml.tbl_researchcall_master s WHERE s.id IN (");
		query.append(researchCallIds.stream().map(id -> "?").collect(Collectors.joining(",")));
		query.append(")");
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet resultSet = null;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement(query.toString());

			int index = 1;
			for (Integer id : researchCallIds) {
				pStmt.setInt(index++, id);
			}
			resultSet = pStmt.executeQuery();
			while (resultSet.next()) {
				ResearchCallModelResponse response = new ResearchCallModelResponse();
				response.setId(resultSet.getInt("id"));
				response.setBasketName(resultSet.getString("basket_name"));
				response.setUserId(resultSet.getString("user_id"));
				response.setExpiryDate(resultSet.getDate("expiry_date"));
				response.setCategory(resultSet.getString("category"));
				response.setSubCategory(resultSet.getString("subcategory"));
				response.setTags(resultSet.getString("tags"));
				response.setShortDescription(resultSet.getString("shortDescription"));
				response.setLongDescription(resultSet.getString("longDescription"));
				response.setIsVendorBasket(resultSet.getString("is_vendor_basket"));
				response.setResearchcall(resultSet.getString("researchcall"));
				response.setVendorCode(resultSet.getString("vendor_code"));
				response.setRemarks(resultSet.getString("remarks"));
				response.setPushNotificationTitle(resultSet.getString("pushnotification_title"));
				response.setSource(resultSet.getString("source"));
				response.setStatus(resultSet.getString("status"));
				response.setSpeclizationTag(resultSet.getString("speclization_tag"));
				response.setCreatedOn(resultSet.getString("created_on"));
				list.add(response);
			}
		} catch (SQLException e) {
			Log.error("Error retrieving data from table: " + e.getMessage());
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (pStmt != null)
					pStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				Log.error("Error closing resources: " + e.getMessage());
			}
		}

		return list;
	}
}
