package in.codifi.basket.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.basket.entity.logs.AccessLogModel;
import in.codifi.basket.entity.logs.RestAccessLogModel;
import in.codifi.basket.utils.StringUtil;
import in.codifi.cache.AccessLogCache;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AccessLogManager {
	@Named("logs")
	@Inject
	DataSource dataSource;

	/**
	 * method to insert access log
	 * 
	 * @author SowmiyaThangaraj
	 * @param accLogModel
	 */
	public void insertAccessLog(AccessLogModel accLogModel) {
		Date inTimeDate;
		if (accLogModel.getInTime() != null) {
			inTimeDate = new Date(accLogModel.getInTime().getTime());
		} else {
			inTimeDate = new Date();
		}
		String date = new SimpleDateFormat("ddMMyyyy").format(inTimeDate);
		String hour = new SimpleDateFormat("HH").format(inTimeDate);
		String tableName = "tbl_" + date + "_access_log_" + hour;
		accLogModel.setTableName(tableName);

		List<AccessLogModel> cacheAccessLogModels = new ArrayList<>(AccessLogCache.getInstance().getBatchAccessModel());
		if (cacheAccessLogModels.size() > 0) {
			if (cacheAccessLogModels.get(0).getTableName().equalsIgnoreCase(tableName)) {
				AccessLogCache.getInstance().getBatchAccessModel().add(accLogModel);
			} else {
				AccessLogCache.getInstance().getBatchAccessModel().clear();
				AccessLogCache.getInstance().setBatchAccessModel(new ArrayList<>());
				insertBatchAccessLog(cacheAccessLogModels);
				AccessLogCache.getInstance().getBatchAccessModel().add(accLogModel);
			}
		} else {
			AccessLogCache.getInstance().getBatchAccessModel().add(accLogModel);
		}

		if (AccessLogCache.getInstance().getBatchAccessModel().size() >= 25) {
			List<AccessLogModel> accessLogModels = new ArrayList<>(AccessLogCache.getInstance().getBatchAccessModel());
			AccessLogCache.getInstance().getBatchAccessModel().clear();
			AccessLogCache.getInstance().setBatchAccessModel(new ArrayList<>());
			insertBatchAccessLog(accessLogModels);
		}
	}

	/**
	 * Method to insert batch access log
	 * 
	 * @author Dinesh Kumar
	 * @param batchLogs
	 */
	public void insertBatchAccessLog(List<AccessLogModel> batchLogs) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			PreparedStatement statement = null;
			Connection connection = null;

			@Override
			public void run() {
				try {
					connection = dataSource.getConnection();
					if (batchLogs != null && batchLogs.size() > 0) {
						String insertQuery = "INSERT INTO " + batchLogs.get(0).getTableName() + " "
								+ " (user_id, ucc, req_id, source, vendor, in_time, out_time, lag_time,  module, method, req_body,"
								+ " res_body, device_ip, user_agent, domain, content_type, session, uri) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
						statement = connection.prepareStatement(insertQuery);
						for (AccessLogModel accLogModel : batchLogs) {
							int paramPos = 1;
							statement.setString(paramPos++, accLogModel.getUserId());
							statement.setString(paramPos++, accLogModel.getUcc());
							statement.setString(paramPos++, accLogModel.getReqId());
							statement.setString(paramPos++, accLogModel.getSource());
							statement.setString(paramPos++, accLogModel.getVendor());
							statement.setTimestamp(paramPos++, accLogModel.getInTime());
							statement.setTimestamp(paramPos++, accLogModel.getOutTime());
							statement.setLong(paramPos++, accLogModel.getLagTime());
							statement.setString(paramPos++, accLogModel.getModule());
							statement.setString(paramPos++, accLogModel.getMethod());
							statement.setString(paramPos++, accLogModel.getReqBody());
							String respBody = "";
							int maxLength = 8192;
							if (StringUtil.isNotNullOrEmpty(accLogModel.getResBody())
									&& accLogModel.getResBody().length() > maxLength) {
								respBody = accLogModel.getResBody().substring(0, maxLength);
							} else {
								respBody = accLogModel.getResBody();
							}
							statement.setString(paramPos++, respBody);
							statement.setString(paramPos++, accLogModel.getDeviceIp());
							statement.setString(paramPos++, accLogModel.getUserAgent());
							statement.setString(paramPos++, accLogModel.getDomain());
							statement.setString(paramPos++, accLogModel.getContentType());
							statement.setString(paramPos++, accLogModel.getSession());
							statement.setString(paramPos++, accLogModel.getUri());
							statement.addBatch();
						}
						statement.executeBatch();
					}
					statement.close();
					connection.close();
				} catch (Exception e) {
					Log.error("TR - Basket - insertAccessLog -" + e);
				} finally {
					try {
						if (statement != null) {
							statement.close();
						}
						if (connection != null) {
							connection.close();
						}
					} catch (Exception e) {
						Log.error("TR - Basket - insertAccessLog -" + e);
					}
				}
			}
		});
	}

	/**
	 * Method to insert rest access log
	 * 
	 * @author Dinesh Kumar
	 * @param accLogModel
	 */
	public void insertRestAccessLog(RestAccessLogModel accLogModel) {

		Date inTimeDate = new Date();
		String date = new SimpleDateFormat("ddMMyyyy").format(inTimeDate);
		String tableName = "tbl_" + date + "_rest_access_log";
		AccessLogCache.getInstance().getBatchRestAccessModel().add(accLogModel);
		if (AccessLogCache.getInstance().getBatchRestAccessModel().size() >= 25) {
			List<RestAccessLogModel> accessLogModels = new ArrayList<>(
					AccessLogCache.getInstance().getBatchRestAccessModel());
			AccessLogCache.getInstance().getBatchRestAccessModel().clear();
			AccessLogCache.getInstance().setBatchRestAccessModel(new ArrayList<>());
			insertBatchRestAccessLog(tableName, accessLogModels);
		}
	}

	/**
	 * Method to insert batch Rest Access Log
	 * 
	 * @author Dinesh kumar
	 * @param tableName
	 * @param batchLogs
	 */
	public void insertBatchRestAccessLog(String tableName, List<RestAccessLogModel> logsData) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			PreparedStatement statement = null;
			Connection connection = null;

			@Override
			public void run() {
				List<RestAccessLogModel> batchLogs = new ArrayList<>();
				batchLogs = logsData;
				try {
					connection = dataSource.getConnection();
					if (batchLogs != null && batchLogs.size() > 0) {

						String insertQuery = "INSERT INTO " + tableName
								+ "(user_id, url, in_time, out_time, total_time, module,"
								+ " method, req_body, res_body,vendor) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

						statement = connection.prepareStatement(insertQuery);
						for (RestAccessLogModel accLogModel : batchLogs) {
							int paramPos = 1;
							statement.setString(paramPos++, accLogModel.getUserId());
							statement.setString(paramPos++, accLogModel.getUrl());
							statement.setTimestamp(paramPos++, accLogModel.getInTime());
							statement.setTimestamp(paramPos++, accLogModel.getOutTime());
							statement.setString(paramPos++, accLogModel.getTotalTime());
							statement.setString(paramPos++, accLogModel.getModule());
							statement.setString(paramPos++, accLogModel.getMethod());
							statement.setString(paramPos++, accLogModel.getReqBody());
							String respBody = "";
							int maxLength = 8192;
							if (StringUtil.isNotNullOrEmpty(accLogModel.getResBody())
									&& accLogModel.getResBody().length() > maxLength) {
								respBody = accLogModel.getResBody().substring(0, maxLength);
							} else {
								respBody = accLogModel.getResBody();
							}
							statement.setString(paramPos++, respBody);
							statement.setString(paramPos++, "TR");
							statement.addBatch();
						}
						statement.executeBatch();
					} else {
						System.out.println("0");
					}

					statement.close();
					connection.close();
				} catch (Exception e) {
					Log.error("TR - Basket - insertRestAccessLog -" + e);
				} finally {
					try {
						if (statement != null) {
							statement.close();
						}
						if (connection != null) {
							connection.close();
						}
					} catch (Exception e) {
						Log.error("TR - Basket - insertRestAccessLog -" + e);
					}
				}
			}
		});
	}

//	public void insertAccessLog(AccessLogModel accLogModel) {
//
//		Date inTimeDate;
//		if (accLogModel.getInTime() != null) {
//			inTimeDate = new Date(accLogModel.getInTime().getTime());
//		} else {
//			inTimeDate = new Date();
//		}
//		Connection connection = null;
//		Statement state = null;
//		PreparedStatement statement = null;
//		String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
//		String hour = new SimpleDateFormat("HH").format(inTimeDate);
//		String tableName = "tbl_" + date + "_access_log_" + hour;
//		try {
//
//			connection = dataSource.getConnection();
//			state = connection.createStatement();
//
//			String insertQuery = "INSERT INTO " + tableName
//					+ "(user_id, ucc, req_id, source, vendor, in_time, out_time, lag_time,  module, method, req_body,"
//					+ " res_body, device_ip, user_agent, domain, content_type, session, uri) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//			statement = connection.prepareStatement(insertQuery);
//			int paramPos = 1;
//			statement.setString(paramPos++, accLogModel.getUserId());
//			statement.setString(paramPos++, accLogModel.getUcc());
//			statement.setString(paramPos++, accLogModel.getReqId());
//			statement.setString(paramPos++, accLogModel.getSource());
//			statement.setString(paramPos++, accLogModel.getVendor());
//			statement.setTimestamp(paramPos++, accLogModel.getInTime());
//			statement.setTimestamp(paramPos++, accLogModel.getOutTime());
//			statement.setLong(paramPos++, accLogModel.getLagTime());
//			statement.setString(paramPos++, accLogModel.getModule());
//			statement.setString(paramPos++, accLogModel.getMethod());
//			statement.setString(paramPos++, accLogModel.getReqBody());
////			statement.setString(paramPos++, accLogModel.getResBody());
//			String respBody = "";
//			int maxLength = 8192;
//			if (StringUtil.isNotNullOrEmpty(accLogModel.getResBody())
//					&& accLogModel.getResBody().length() > maxLength) {
//				respBody = accLogModel.getResBody().substring(0, maxLength);
//			} else {
//				respBody = accLogModel.getResBody();
//			}
//			statement.setString(paramPos++, respBody);
//			statement.setString(paramPos++, accLogModel.getDeviceIp());
//			statement.setString(paramPos++, accLogModel.getUserAgent());
//			statement.setString(paramPos++, accLogModel.getDomain());
//			statement.setString(paramPos++, accLogModel.getContentType());
//			statement.setString(paramPos++, accLogModel.getSession());
//			statement.setString(paramPos++, accLogModel.getUri());
//			statement.executeUpdate();
//
//			statement.close();
//			state.close();
//			connection.close();
//		} catch (Exception e) {
//			Log.error("TR - Basket - insertAccessLog -" + e);
//		} finally {
//			try {
//				if (statement != null) {
//					statement.close();
//				}
//				if (state != null) {
//					state.close();
//				}
//				if (connection != null) {
//					connection.close();
//				}
//			} catch (Exception e) {
//				Log.error("TR - Basket - insertAccessLog -" + e);
//			}
//		}
//
//	}
//
//	/**
//	 * 
//	 * Method to insert rest access log
//	 * 
//	 * @author Dinesh Kumar
//	 *
//	 * @param accLogModel
//	 */
//	public void insertRestAccessLog(RestAccessLogModel accLogModel) {
//
//		Date inTimeDate = new Date();
//
//		String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
//		String tableName = "tbl_" + date + "_rest_access_log";
//		PreparedStatement statement = null;
//		Connection connection = null;
//		Statement state = null;
//		try {
//
//			connection = dataSource.getConnection();
//			state = connection.createStatement();
//
//			String insertQuery = "INSERT INTO " + tableName + "(user_id, url, in_time, out_time, total_time, module,"
//					+ " method, req_body, res_body,vendor) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//			statement = connection.prepareStatement(insertQuery);
//			int paramPos = 1;
//			statement.setString(paramPos++, accLogModel.getUserId());
//			statement.setString(paramPos++, accLogModel.getUrl());
//			statement.setTimestamp(paramPos++, accLogModel.getInTime());
//			statement.setTimestamp(paramPos++, accLogModel.getOutTime());
//			statement.setString(paramPos++, accLogModel.getTotalTime());
//			statement.setString(paramPos++, accLogModel.getModule());
//			statement.setString(paramPos++, accLogModel.getMethod());
//			statement.setString(paramPos++, accLogModel.getReqBody());
////			statement.setString(paramPos++, accLogModel.getResBody());
//			String respBody = "";
//			int maxLength = 8192;
//			if (StringUtil.isNotNullOrEmpty(accLogModel.getResBody())
//					&& accLogModel.getResBody().length() > maxLength) {
//				respBody = accLogModel.getResBody().substring(0, maxLength);
//			} else {
//				respBody = accLogModel.getResBody();
//			}
//			statement.setString(paramPos++, respBody);
//			statement.setString(paramPos++, "TR");
//			statement.executeUpdate();
//
//			statement.close();
//			state.close();
//			connection.close();
//		} catch (Exception e) {
//			Log.error("TR - Basket - insertRestAccessLog -" + e);
//		} finally {
//			try {
//				if (statement != null) {
//					statement.close();
//				}
//				if (state != null) {
//					state.close();
//				}
//				if (connection != null) {
//					connection.close();
//				}
//			} catch (Exception e) {
//				Log.error("TR - Basket - insertRestAccessLog -" + e);
//			}
//		}
//	}
}