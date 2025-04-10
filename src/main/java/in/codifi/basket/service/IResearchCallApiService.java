package in.codifi.basket.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;

import in.codifi.basket.config.ConcorentHashMapConfig;
import in.codifi.basket.dao.ResearchCallDAO;
import in.codifi.basket.entity.primary.ReasearchCallUsers;
import in.codifi.basket.entity.primary.ResearchcallScripEntity;
import in.codifi.basket.model.request.ResearchCallRequest;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.repository.DeviceMappingRepository;
import in.codifi.basket.repository.ResearchOrderRepository;
import in.codifi.basket.repository.ResearchcallScripRepository;
import in.codifi.basket.repository.ResearchcallUserRepository;
import in.codifi.basket.repository.VendorAppRepository;
import in.codifi.basket.repository.researchcallStatusRepository;
import in.codifi.basket.service.spec.IResearchCallApiServiceSpec;
import in.codifi.basket.utils.AppConstants;
import in.codifi.basket.utils.AppUtil;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.utils.PushNoficationUtils;
import in.codifi.basket.utils.StringUtil;
import in.codifi.basket.utils.ValidateUtil;
import in.codifi.basket.ws.model.ResearchCallModelResponse;

@ApplicationScoped
public class IResearchCallApiService implements IResearchCallApiServiceSpec {
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	ResearchOrderRepository researchcallOrderRepository;
	@Inject
	DeviceMappingRepository deviceMappingRepository;
	@Inject
	ResearchcallScripRepository researchcallScripRepository;
	@Inject
	AppUtil appUtil;
	@Inject
	VendorAppRepository vendorAppRepository;
	@Inject
	UserNotificationService userNotificationSpec;
	@Inject
	PushNoficationUtils pushNoficationUtils;
	@Inject
	ConcorentHashMapConfig config;
	@Inject
	researchcallStatusRepository statusRepository;
	@Inject
	ResearchCallDAO researchCallDAO;
	@Inject
	ValidateUtil validateUtil;
	@Inject
	ResearchcallUserRepository researchcallUserRepo;

//	@Override
//	public RestResponse<GenericResponse> getResWithBasketDetails(ResearchCallRequest pReq, String pClientId) {
//		try {
//			if (StringUtil.isNullOrEmpty(pClientId)) {
//				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//			}
//
//			List<ResearchCallModelResponse> orderEntities = researchCallDAO.getBasketData(pReq, pClientId,
//					AppConstants.ALL);
//			if (!orderEntities.isEmpty()) {
//				Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new HashMap<>();
//
//				for (ResearchCallModelResponse orderEntity : orderEntities) {
//					List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
//							.getByResearchcallId(orderEntity.getId());
//
//					// Iterate through each scrip for this order
//					for (ResearchcallScripEntity scrip : scripEntities) {
//						// Create the response map for the current scrip
//						Map<String, Object> scripDetails = new HashMap<>();
//						scripDetails.put("basketId", orderEntity.getId());
//						scripDetails.put("lotSize", scrip.getLotSize());
//						scripDetails.put("exchange", scrip.getExchange());
//						scripDetails.put("token", scrip.getToken());
//						scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
//						scripDetails.put("qty", scrip.getQty());
//						scripDetails.put("expiry", scrip.getExpiry());
//						scripDetails.put("product", scrip.getProduct());
//						scripDetails.put("transType", scrip.getTransType());
//						scripDetails.put("priceType", scrip.getPriceType());
//						scripDetails.put("orderType", scrip.getOrderType());
//						scripDetails.put("ret", scrip.getRetention());
//						scripDetails.put("triggerPrice", scrip.getTriggerPrice());
//						scripDetails.put("disClosedQty", scrip.getDisClosedQty());
//						scripDetails.put("mktProtection", scrip.getMktProtection());
////						scripDetails.put("status", "open"); // Status as string
//						scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
//						scripDetails.put("formattedInsName", scrip.getFormattedInsName());
//						scripDetails.put("weekTag", scrip.getWeekTag());
//						scripDetails.put("validityDays", scrip.getValidityDays());
//						scripDetails.put("expiryDate", scrip.getExpiryDate());
////						scripDetails.put("speclizationTag", scrip.getSpeclizationTag());
//
//						// Group the scrip by category and subcategory
//						String category = orderEntity.getCategory();
//						String subCategory = orderEntity.getSubCategory();
//
//						categoryMap.computeIfAbsent(category, k -> new HashMap<>());
//						categoryMap.get(category).computeIfAbsent(subCategory, k -> new ArrayList<>())
//								.add(scripDetails);
//					}
//				}
//
//				// Prepare the response object
//				List<Map<String, Object>> resultList = new ArrayList<>();
//				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : categoryMap.entrySet()) {
//					Map<String, Object> categoryDetails = new HashMap<>();
//					categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
//					resultList.add(categoryDetails);
//				}
//
//				return prepareResponse.prepareSuccessResponseObject(resultList);
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
//			}
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.NULL_POINTER_EXCEPTION);
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.DATABASE_ERROR);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
//		}
//	}

//	@Override
//	public RestResponse<GenericResponse> getResearchCall(ResearchCallRequest pReq, String pClientId) {
//		try {
//			if (StringUtil.isNullOrEmpty(pClientId)) {
//				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//			}
//
//			List<ResearchCallModelResponse> orderEntities = researchCallDAO.getBasketData1(pReq, pClientId,
//					AppConstants.ALL);
//			if (!orderEntities.isEmpty()) {
//				Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new HashMap<>();
//
//				for (ResearchCallModelResponse orderEntity : orderEntities) {
//					List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
//							.getByResearchcallId(orderEntity.getId());
//
//					// Group scrips into a list
//					List<Map<String, Object>> scripDetailsList = new ArrayList<>();
//					for (ResearchcallScripEntity scrip : scripEntities) {
//						Map<String, Object> scripDetails = new HashMap<>();
//						scripDetails.put("mktProtection", scrip.getMktProtection());
//						scripDetails.put("orderType", scrip.getOrderType());
//						scripDetails.put("disClosedQty", scrip.getDisClosedQty());
//						scripDetails.put("expiry", scrip.getExpiry());
//						scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
//						scripDetails.put("validityDays", scrip.getValidityDays());
//						scripDetails.put("ret", scrip.getRetention());
//						scripDetails.put("product", scrip.getProduct());
//						scripDetails.put("triggerPrice", scrip.getTriggerPrice());
//						scripDetails.put("lotSize", scrip.getLotSize());
//						scripDetails.put("weekTag", scrip.getWeekTag());
//						scripDetails.put("priceType", scrip.getPriceType());
//						scripDetails.put("formattedInsName", scrip.getFormattedInsName());
//						scripDetails.put("token", scrip.getToken());
//						scripDetails.put("transType", scrip.getTransType());
//						scripDetails.put("sortOrder", scrip.getSortOrder());
//						scripDetails.put("qty", scrip.getQty());
//						scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
//						scripDetails.put("exchange", scrip.getExchange());
//						scripDetailsList.add(scripDetails);
//					}
//
//					// Create the basket details map
////					Map<String, Object> basketDetails = new HashMap<>();
//					Map<String, Object> basketDetails = new LinkedHashMap<>();
//					basketDetails.put("basketId", orderEntity.getId());
//					basketDetails.put("createdOn", orderEntity.getCreatedOn());
//					basketDetails.put("expiryDate", orderEntity.getExpiryDate());
//					basketDetails.put("speclizationTag", orderEntity.getSpeclizationTag());
//					basketDetails.put("status", orderEntity.getStatus());
//					basketDetails.put("description", orderEntity.getLongDescription());
//					basketDetails.put("shortDesc", orderEntity.getShortDescription());
//					basketDetails.put("remarks", ""); // Placeholder
//					basketDetails.put("scripdetails", scripDetailsList);
//
//					// Group baskets by category and subcategory
//					String category = orderEntity.getCategory();
//					String subCategory = orderEntity.getSubCategory();
//
//					categoryMap.computeIfAbsent(category, k -> new HashMap<>());
//					categoryMap.get(category).computeIfAbsent(subCategory, k -> new ArrayList<>()).add(basketDetails);
//				}
//
//				// Build the final result list
//				List<Map<String, Object>> resultList = new ArrayList<>();
//				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : categoryMap.entrySet()) {
//					Map<String, Object> categoryDetails = new HashMap<>();
//					categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
//					resultList.add(categoryDetails);
//				}
//
//				// Use the prepareSuccessResponseObject method
//				return prepareResponse.prepareSuccessResponseObject(resultList);
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
//			}
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.NULL_POINTER_EXCEPTION);
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.DATABASE_ERROR);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
//		}
//	}

	/**
	 *
	 * Method to get researchcall details
	 *
	 * @author gokul
	 *
	 * @param pReq
	 * @return
	 */
//	@Override
//	public RestResponse<GenericResponse> getResearchCall(ResearchCallRequest pReq, String pClientId) {
//		try {
//			if (StringUtil.isNullOrEmpty(pClientId)) {
//				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//			}
//
//			List<ResearchCallModelResponse> orderEntities = researchCallDAO.getBasketData1(pReq, pClientId,
//					AppConstants.ALL);
//			if (!orderEntities.isEmpty()) {
//				Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new HashMap<>();
//
//				for (ResearchCallModelResponse orderEntity : orderEntities) {
//					List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
//							.getByResearchcallId(orderEntity.getId());
//
//					// Group scrips into a list
//					List<Map<String, Object>> scripDetailsList = new ArrayList<>();
//					for (ResearchcallScripEntity scrip : scripEntities) {
//						Map<String, Object> scripDetails = new LinkedHashMap<>();
//						scripDetails.put("scripId", scrip.getId());
//						scripDetails.put("orderType", scrip.getOrderType());
//						scripDetails.put("priceUpperBound", scrip.getPriceUpperBound());
//						scripDetails.put("priceLowerBound", scrip.getPriceLowerBound());
//						scripDetails.put("triggerPrice", scrip.getTriggerPrice());
//						scripDetails.put("targetUpperBound", scrip.getTargetUpperBound());
//						scripDetails.put("targetLowerBound", scrip.getTargetLowerBound());
//						scripDetails.put("stopLossUpperBound", scrip.getStopLossUpperBound());
//						scripDetails.put("stopLossLowerBound", scrip.getStopLossLowerBound());
//						scripDetails.put("disClosedQty", scrip.getDisClosedQty());
//						scripDetails.put("expiry", scrip.getExpiry());
//						scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
//						scripDetails.put("validityDays", scrip.getValidityDays());
//						scripDetails.put("mktProtection", scrip.getMktProtection());
//						scripDetails.put("ret", scrip.getRetention());
//						scripDetails.put("product", scrip.getProduct());
//						scripDetails.put("lotSize", scrip.getLotSize());
//						scripDetails.put("weekTag", scrip.getWeekTag());
//						scripDetails.put("priceType", scrip.getPriceType());
//						scripDetails.put("formattedInsName", scrip.getFormattedInsName());
//						scripDetails.put("token", scrip.getToken());
//						scripDetails.put("transType", scrip.getTransType());
//						scripDetails.put("sortOrder", scrip.getSortOrder());
//						scripDetails.put("qty", scrip.getQty());
//						scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
//						scripDetails.put("exchange", scrip.getExchange());
//						scripDetailsList.add(scripDetails);
//					}
//					// Create the basket details map
////					Map<String, Object> basketDetails = new HashMap<>();
//					Map<String, Object> basketDetails = new LinkedHashMap<>();
//					basketDetails.put("basketId", orderEntity.getId());
//					basketDetails.put("createdOn", orderEntity.getCreatedOn());
//					basketDetails.put("expiryDate", orderEntity.getExpiryDate());
//					basketDetails.put("speclizationTag", orderEntity.getSpeclizationTag());
//					basketDetails.put("status", orderEntity.getStatus());
//					basketDetails.put("description", orderEntity.getLongDescription());
//					basketDetails.put("shortDesc", orderEntity.getShortDescription());
//					basketDetails.put("remarks", ""); // Placeholder
//					basketDetails.put("scripdetails", scripDetailsList);
//
//					// Group baskets by category and subcategory
//					String category = orderEntity.getCategory();
//					String subCategory = orderEntity.getSubCategory();
//
//					categoryMap.computeIfAbsent(category, k -> new HashMap<>());
//					categoryMap.get(category).computeIfAbsent(subCategory, k -> new ArrayList<>()).add(basketDetails);
//				}
//
//				// Build the final result list
//				List<Map<String, Object>> resultList = new ArrayList<>();
//				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : categoryMap.entrySet()) {
//					Map<String, Object> categoryDetails = new HashMap<>();
//					categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
//					resultList.add(categoryDetails);
//				}
//
//				// Use the prepareSuccessResponseObject method
//				return prepareResponse.prepareSuccessResponseObject(resultList);
//			} else {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
//			}
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.NULL_POINTER_EXCEPTION);
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.DATABASE_ERROR);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
//		}
//	}

	@Override
	public RestResponse<GenericResponse> getResearchCall(String pClientId) {
		try {
			if (StringUtil.isNullOrEmpty(pClientId)) {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

			// Retrieve basket data
			List<ResearchCallModelResponse> orderEntities = researchCallDAO.getBasketData1(pClientId);
			if (!orderEntities.isEmpty()) {
				// Create a map to store category sort orders
				Map<String, Integer> categorySortOrders = new HashMap<>();

				// Use LinkedHashMap to maintain insertion order
				Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new LinkedHashMap<>();

				for (ResearchCallModelResponse orderEntity : orderEntities) {
					// Store category sort order
					categorySortOrders.put(orderEntity.getCategory(), orderEntity.getSortOrder());

					List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
							.getByResearchcallId(orderEntity.getId());

					// Create a list for scrip details
					List<Map<String, Object>> scripDetailsList = new ArrayList<>();
					for (ResearchcallScripEntity scrip : scripEntities) {
						Map<String, Object> scripDetails = new LinkedHashMap<>();
						scripDetails.put("scripId", scrip.getId());
						scripDetails.put("orderType", scrip.getOrderType());
						scripDetails.put("priceUpperBound", scrip.getPriceUpperBound());
						scripDetails.put("priceLowerBound", scrip.getPriceLowerBound());
						scripDetails.put("triggerPrice", scrip.getTriggerPrice());
						scripDetails.put("targetUpperBound", scrip.getTargetUpperBound());
						scripDetails.put("targetLowerBound", scrip.getTargetLowerBound());
						scripDetails.put("stopLossUpperBound", scrip.getStopLossUpperBound());
						scripDetails.put("stopLossLowerBound", scrip.getStopLossLowerBound());
						scripDetails.put("disClosedQty", scrip.getDisClosedQty());
						scripDetails.put("expiry", scrip.getExpiry());
						scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
						scripDetails.put("validityDays", scrip.getValidityDays());
						scripDetails.put("mktProtection", scrip.getMktProtection());
						scripDetails.put("ret", scrip.getRetention());
						scripDetails.put("product", scrip.getProduct());
						scripDetails.put("lotSize", scrip.getLotSize());
						scripDetails.put("weekTag", scrip.getWeekTag());
						scripDetails.put("priceType", scrip.getPriceType());
						scripDetails.put("formattedInsName", scrip.getFormattedInsName());
						scripDetails.put("token", scrip.getToken());
						scripDetails.put("transType", scrip.getTransType());
//						scripDetails.put("sortOrder", scrip.getSortOrder());
						scripDetails.put("qty", scrip.getQty());
						scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
						scripDetails.put("exchange", scrip.getExchange());
						scripDetailsList.add(scripDetails);
					}

					// Prepare the basket details
					Map<String, Object> basketDetails = new LinkedHashMap<>();
					basketDetails.put("basketId", orderEntity.getId());
					basketDetails.put("createdOn", orderEntity.getCreatedOn());
					basketDetails.put("expiryDate", orderEntity.getExpiryDate());
					basketDetails.put("status", orderEntity.getStatus());
					basketDetails.put("speclizationTag", orderEntity.getSpeclizationTag());
					basketDetails.put("description", orderEntity.getLongDescription());
					basketDetails.put("shortDesc", orderEntity.getShortDescription());
					basketDetails.put("sortOrder", orderEntity.getSortOrder()); // Add sortOrder to basket details
					basketDetails.put("scripdetails", scripDetailsList);

					// Group by category and subcategory
					String category = orderEntity.getCategory();
					String subCategory = orderEntity.getSubCategory();
					categoryMap.computeIfAbsent(category, k -> new LinkedHashMap<>());
					categoryMap.get(category).computeIfAbsent(subCategory, k -> new ArrayList<>()).add(basketDetails);
				}

				// Sort categories based on their sort orders and create final result
				List<Map<String, Object>> resultList = new ArrayList<>();

				// Convert categoryMap entries to a list and sort by category sort order
				List<Map.Entry<String, Map<String, List<Map<String, Object>>>>> sortedCategories = new ArrayList<>(
						categoryMap.entrySet());

				sortedCategories.sort(Comparator
						.comparingInt(entry -> categorySortOrders.getOrDefault(entry.getKey(), Integer.MAX_VALUE)));

				// Build the final result list with sorted categories
				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : sortedCategories) {
					Map<String, Object> categoryDetails = new LinkedHashMap<>();

					// Sort the baskets within each subcategory
					for (List<Map<String, Object>> baskets : categoryEntry.getValue().values()) {
						baskets.sort(Comparator.comparingInt(basket -> (Integer) basket.get("sortOrder")));
					}

					categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
					resultList.add(categoryDetails);
				}

				return prepareResponse.prepareSuccessResponseObject(resultList);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
		}
	}

	@Override
	public RestResponse<GenericResponse> getResWithBasketDetails(ResearchCallRequest pReq, String pClientId) {
		try {
			if (StringUtil.isNullOrEmpty(pClientId)) {
				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
			}

			// Initialize userIds list with pClientId and "ALL"
			List<String> userIds = new ArrayList<>();
			userIds.add(pClientId);
			if (!userIds.contains("ALL")) {
				userIds.add("ALL");
			}

			// Fetch mapped research call user data
			List<ReasearchCallUsers> researchOrders = researchcallUserRepo.findByUserIdOrAll(userIds);

			// Extract research call IDs
			List<Integer> researchCallIds = researchOrders.stream().map(ReasearchCallUsers::getResearchCallId)
					.distinct().collect(Collectors.toList());

			if (researchCallIds.isEmpty()) {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
			}

			// Fetch basket data using extracted research call IDs
			List<ResearchCallModelResponse> orderEntities = researchCallDAO
					.getBasketDataByResearchCallIds(researchCallIds);

			if (!orderEntities.isEmpty()) {
				Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new HashMap<>();

				for (ResearchCallModelResponse orderEntity : orderEntities) {
					List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
							.getByResearchcallId(orderEntity.getId());

					for (ResearchcallScripEntity scrip : scripEntities) {
						Map<String, Object> scripDetails = new HashMap<>();
						scripDetails.put("basketId", orderEntity.getId());
						scripDetails.put("lotSize", scrip.getLotSize());
						scripDetails.put("exchange", scrip.getExchange());
						scripDetails.put("token", scrip.getToken());
						scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
						scripDetails.put("qty", scrip.getQty());
						scripDetails.put("expiry", scrip.getExpiry());
						scripDetails.put("product", scrip.getProduct());
						scripDetails.put("transType", scrip.getTransType());
						scripDetails.put("priceType", scrip.getPriceType());
						scripDetails.put("orderType", scrip.getOrderType());
						scripDetails.put("ret", scrip.getRetention());
						scripDetails.put("triggerPrice", scrip.getTriggerPrice());
						scripDetails.put("disClosedQty", scrip.getDisClosedQty());
						scripDetails.put("mktProtection", scrip.getMktProtection());
						scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
						scripDetails.put("formattedInsName", scrip.getFormattedInsName());
						scripDetails.put("weekTag", scrip.getWeekTag());
						scripDetails.put("validityDays", scrip.getValidityDays());
						scripDetails.put("expiryDate", scrip.getExpiryDate());
						// Grouping by category and subcategory
						String category = orderEntity.getCategory();
						String subCategory = orderEntity.getSubCategory();

						categoryMap.computeIfAbsent(category, k -> new HashMap<>())
								.computeIfAbsent(subCategory, k -> new ArrayList<>()).add(scripDetails);
					}
				}

				// Prepare response
				List<Map<String, Object>> resultList = new ArrayList<>();
				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : categoryMap.entrySet()) {
					Map<String, Object> categoryDetails = new HashMap<>();
					categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
					resultList.add(categoryDetails);
				}

				return prepareResponse.prepareSuccessResponseObject(resultList);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return prepareResponse.prepareFailedResponse(AppConstants.NULL_POINTER_EXCEPTION);
		} catch (DatabaseException e) {
			e.printStackTrace();
			return prepareResponse.prepareFailedResponse(AppConstants.DATABASE_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public RestResponse<GenericResponse> getResearchCall(String pClientId) {
//		try {
//			if (StringUtil.isNullOrEmpty(pClientId)) {
//				return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
//			}
//			// Initialize userIds list
//			List<String> userIds = new ArrayList<>();
//			userIds.add(pClientId);
//			if (!userIds.contains("ALL")) {
//				userIds.add("ALL");
//			}
//			// Fetch mapped research call user data
//			List<ReasearchCallUsers> researchOrders = researchcallUserRepo.findByUserIdOrAll(userIds);
//			// Extract research call IDs
//			List<Integer> researchCallIds = researchOrders.stream().map(ReasearchCallUsers::getResearchCallId)
//					.distinct().collect(Collectors.toList());
//			if (researchCallIds.isEmpty()) {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
//			}
//			// Retrieve research call details
//			List<ResearchCallModelResponse> orderEntities = researchCallDAO.getBasketData1(researchCallIds);
//
//			if (orderEntities.isEmpty()) {
//				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
//			}
//			// Create category map for structured response
//			Map<String, Integer> categorySortOrders = new HashMap<>();
//			Map<String, Map<String, List<Map<String, Object>>>> categoryMap = new LinkedHashMap<>();
//			for (ResearchCallModelResponse orderEntity : orderEntities) {
//				// Store category sort order
//				categorySortOrders.put(orderEntity.getCategory(), orderEntity.getSortOrder());
//				// Fetch scrip details for each research call
//				List<ResearchcallScripEntity> scripEntities = researchcallScripRepository
//						.getByResearchcallId(orderEntity.getId());
//				List<Map<String, Object>> scripDetailsList = new ArrayList<>();
//				for (ResearchcallScripEntity scrip : scripEntities) {
//					Map<String, Object> scripDetails = new LinkedHashMap<>();
//					scripDetails.put("scripId", scrip.getId());
//					scripDetails.put("orderType", scrip.getOrderType());
//					scripDetails.put("priceUpperBound", scrip.getPriceUpperBound());
//					scripDetails.put("priceLowerBound", scrip.getPriceLowerBound());
//					scripDetails.put("triggerPrice", scrip.getTriggerPrice());
//					scripDetails.put("targetUpperBound", scrip.getTargetUpperBound());
//					scripDetails.put("targetLowerBound", scrip.getTargetLowerBound());
//					scripDetails.put("stopLossUpperBound", scrip.getStopLossUpperBound());
//					scripDetails.put("stopLossLowerBound", scrip.getStopLossLowerBound());
//					scripDetails.put("disClosedQty", scrip.getDisClosedQty());
//					scripDetails.put("expiry", scrip.getExpiry());
//					scripDetails.put("tradingSymbol", scrip.getTradingSymbol());
//					scripDetails.put("validityDays", scrip.getValidityDays());
//					scripDetails.put("mktProtection", scrip.getMktProtection());
//					scripDetails.put("ret", scrip.getRetention());
//					scripDetails.put("product", scrip.getProduct());
//					scripDetails.put("lotSize", scrip.getLotSize());
//					scripDetails.put("weekTag", scrip.getWeekTag());
//					scripDetails.put("priceType", scrip.getPriceType());
//					scripDetails.put("formattedInsName", scrip.getFormattedInsName());
//					scripDetails.put("token", scrip.getToken());
//					scripDetails.put("transType", scrip.getTransType());
//					scripDetails.put("qty", scrip.getQty());
//					scripDetails.put("trailingStopLoss", scrip.getTrailingStopLoss());
//					scripDetails.put("exchange", scrip.getExchange());
//					scripDetailsList.add(scripDetails);
//				}
//				// Prepare basket details
//				Map<String, Object> basketDetails = new LinkedHashMap<>();
//				basketDetails.put("basketId", orderEntity.getId());
//				basketDetails.put("createdOn", orderEntity.getCreatedOn());
//				basketDetails.put("expiryDate", orderEntity.getExpiryDate());
//				basketDetails.put("status", orderEntity.getStatus());
//				basketDetails.put("speclizationTag", orderEntity.getSpeclizationTag());
//				basketDetails.put("description", orderEntity.getLongDescription());
//				basketDetails.put("shortDesc", orderEntity.getShortDescription());
//				basketDetails.put("sortOrder", orderEntity.getSortOrder());
//				basketDetails.put("scripdetails", scripDetailsList);
//				// Group by category and subcategory
//				String category = orderEntity.getCategory();
//				String subCategory = orderEntity.getSubCategory();
//				categoryMap.computeIfAbsent(category, k -> new LinkedHashMap<>());
//				categoryMap.get(category).computeIfAbsent(subCategory, k -> new ArrayList<>()).add(basketDetails);
//			}
////			// Sort categories based on sort orders
////			List<Map<String, Object>> resultList = new ArrayList<>();
////			List<Map.Entry<String, Map<String, List<Map<String, Object>>>>> sortedCategories = new ArrayList<>(
////					categoryMap.entrySet());
////			sortedCategories.sort(Comparator
////					.comparingInt(entry -> categorySortOrders.getOrDefault(entry.getKey(), Integer.MAX_VALUE)));
////			for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : sortedCategories) {
////				Map<String, Object> categoryDetails = new LinkedHashMap<>();
////				for (List<Map<String, Object>> baskets : categoryEntry.getValue().values()) {
////					baskets.sort(Comparator.comparingInt(basket -> (Integer) basket.get("sortOrder")));
////				}
////				categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
////				resultList.add(categoryDetails);
////			}
//
//			List<Map<String, Object>> resultList = new ArrayList<>();
//			List<Map.Entry<String, Map<String, List<Map<String, Object>>>>> sortedCategories = new ArrayList<>(
//					categoryMap.entrySet());
//			sortedCategories.sort(Comparator
//					.comparingInt(entry -> categorySortOrders.getOrDefault(entry.getKey(), Integer.MAX_VALUE)));
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
//			for (Map.Entry<String, Map<String, List<Map<String, Object>>>> categoryEntry : sortedCategories) {
//				Map<String, Object> categoryDetails = new LinkedHashMap<>();
//				for (List<Map<String, Object>> baskets : categoryEntry.getValue().values()) {
//					baskets.sort(
//							Comparator.comparingInt(basket -> (Integer) ((Map<String, Object>) basket).get("sortOrder"))
//									.thenComparing(basket -> LocalDateTime
//											.parse((String) ((Map<String, Object>) basket).get("createdOn"), formatter),
//											Comparator.reverseOrder() // DESCENDING ORDER for createdOn
//									));
//				}
//				categoryDetails.put(categoryEntry.getKey(), categoryEntry.getValue());
//				resultList.add(categoryDetails);
//			}
//			return prepareResponse.prepareSuccessResponseObject(resultList);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return prepareResponse.prepareFailedResponse(AppConstants.GENERAL_ERROR);
//		}
//	}

	/**
	 * 
	 * Method to retrieve unique status
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getUniqStatus() {
		try {
			// Fetch entities based on the basketId from the request
			List<String> uniqStatus = researchCallDAO.getUniqStatus();

			if (uniqStatus == null || uniqStatus.isEmpty()) {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_DATA_FOUND);
			}

			// Return success response if status is updated
			return prepareResponse.prepareSuccessResponseObject(uniqStatus);

		} catch (Exception e) {
			// Log the exception for debugging purposes
			e.printStackTrace();

			// Return a failed response in case of an exception
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
	}
}