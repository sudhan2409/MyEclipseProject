package in.codifi.basket.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.entity.primary.BasketNameEntity;
import in.codifi.basket.entity.primary.BasketScripEntity;
import in.codifi.basket.entity.primary.DeviceMappingEntity;
import in.codifi.basket.entity.primary.NotificationReportEntity;
import in.codifi.basket.entity.primary.VendorAppEntity;
import in.codifi.basket.model.request.AdminBasketOrderReq;
import in.codifi.basket.model.request.BasketOrderReq;
import in.codifi.basket.model.request.ScripRequestModel;
import in.codifi.basket.model.request.SendNoficationReqModel;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.repository.AliceBlueEntityManager;
import in.codifi.basket.repository.BasketOrderEntityManager;
import in.codifi.basket.repository.BasketOrderRepository;
import in.codifi.basket.repository.BasketScripRepository;
import in.codifi.basket.repository.DeviceMappingRepository;
import in.codifi.basket.repository.NotificationReportRepository;
import in.codifi.basket.repository.UserNotificationRepository;
import in.codifi.basket.service.spec.IBasketOrderApiService;
import in.codifi.basket.service.spec.UserNotificationSpec;
import in.codifi.basket.utils.AppConstants;
import in.codifi.basket.utils.AppUtil;
import in.codifi.basket.utils.CodifiUtil;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.utils.PushNoficationUtils;
import in.codifi.basket.utils.StringUtil;
import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.cache.model.ContractMasterModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class BasketOrderApiService implements IBasketOrderApiService {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	BasketOrderRepository basketOrderRepository;
	@Inject
	BasketOrderEntityManager basketOrderEntityManager;
	@Inject
	DeviceMappingRepository deviceMappingRepository;
	@Inject
	UserNotificationSpec userNotificationSpec;
	@Inject
	PushNoficationUtils pushNoficationUtils;
	@Inject
	AppUtil appUtil;
	@Inject
	BasketScripRepository basketScripRepository;
	@Inject
	AliceBlueEntityManager entityManager;
	@Inject
	CodifiUtil codifiUtil;
	@Inject
	UserNotificationRepository userNotificationRepository;
	@Inject
	NotificationReportRepository notificationReportRepo;

	/**
	 * Method to Create Basket from admin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> adminCreateBasketName(AdminBasketOrderReq pReq) {
		try {
			String validVendor = validateTppAuthorizedVendor(pReq.getApiKey());
			if (!validVendor.equalsIgnoreCase(AppConstants.VALID_VENDOR))
				return prepareResponse.prepareFailedResponse(validVendor);
			if (StringUtil.isNullOrEmpty(pReq.getBasketName()) && pReq.getExpiryDate() == null)
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			if (pReq.getScrips().size() > 20)
				return prepareResponse.prepareFailedResponse(AppConstants.MORE_THAN_MAXIMUM_SIZE);
			for (ScripRequestModel scripReqModel : pReq.getScrips()) {
				if (StringUtil.isNullOrEmpty(scripReqModel.getExchange())
						|| StringUtil.isNullOrEmpty(scripReqModel.getToken())
						|| StringUtil.isNullOrEmpty(scripReqModel.getTradingSymbol())
						|| StringUtil.isNullOrEmpty(scripReqModel.getQty())
						|| StringUtil.isNullOrEmpty(scripReqModel.getPrice())
						|| StringUtil.isNullOrEmpty(scripReqModel.getProduct())
						|| StringUtil.isNullOrEmpty(scripReqModel.getTransType())
						|| StringUtil.isNullOrEmpty(scripReqModel.getPriceType())
						|| StringUtil.isNullOrEmpty(scripReqModel.getOrderType())
						|| StringUtil.isNullOrEmpty(scripReqModel.getRet())
						|| StringUtil.isNullOrEmpty(scripReqModel.getSource())) {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}

			for (ScripRequestModel scripReqModel : pReq.getScrips()) {
				if (!(scripReqModel.getExchange().equalsIgnoreCase("NSE")
						|| scripReqModel.getExchange().equalsIgnoreCase("BSE")
						|| scripReqModel.getExchange().equalsIgnoreCase("NFO")
						|| scripReqModel.getExchange().equalsIgnoreCase("BFO")
						|| scripReqModel.getExchange().equalsIgnoreCase("CDS")
						|| scripReqModel.getExchange().equalsIgnoreCase("BCD")
						|| scripReqModel.getExchange().equalsIgnoreCase("MCX"))) {
					return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
				}
			}
			/** Get basketId to insert **/
			BasketNameEntity basketNameEntity = new BasketNameEntity();
			List<String> userIdList = new ArrayList<>();
			if (!pReq.getUserId().isEmpty()) {
				userIdList = pReq.getUserId();
			} else {
				userIdList = deviceMappingRepository.getDistinctUserIds();
			}
			/** Basket creation for mentioned user **/
			for (String userId : userIdList) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm");
				String current_date = dateFormat.format(new Date());
				BasketNameEntity entity = new BasketNameEntity();
				entity.setBasketId(getMaxBasketId());
				entity.setUserId(userId);
				entity.setDescription(pReq.getDescription());
				entity.setVendorCode(pReq.getApiKey());
				entity.setExpiryDate(pReq.getExpiryDate());
				entity.setIsVendorBasket(1);
				entity.setCreatedBy(userId);
				entity.setBasketName(pReq.getBasketName() + "_" + current_date);
				/** Method to create basket **/
				basketNameEntity = basketOrderRepository.save(entity);
				List<BasketScripEntity> scripList = new ArrayList<>();
				for (ScripRequestModel scrips : pReq.getScrips()) {
					BasketScripEntity scripEntity = new BasketScripEntity();
					scripEntity.setBasketId(basketNameEntity.getBasketId());
					scripEntity.setDisClosedQty(scrips.getDisClosedQty());
					scripEntity.setExchange(scrips.getExchange());
					scripEntity.setExpiry(scrips.getExpiry());
					scripEntity.setFormattedInsName(scrips.getFormattedInsName());
					scripEntity.setMktProtection(scrips.getMktProtection());
					scripEntity.setOrderType(scrips.getOrderType());
					scripEntity.setPrice(scrips.getPrice());
					scripEntity.setPriceType(scrips.getPriceType());
					scripEntity.setProduct(scrips.getProduct());
					scripEntity.setQty(scrips.getQty());
					scripEntity.setRet(scrips.getRet());
					scripEntity.setSortOrder(scrips.getSortOrder());
					scripEntity.setStopLoss(scrips.getStopLoss());
					scripEntity.setTarget(scrips.getTarget());
					scripEntity.setToken(scrips.getToken());
					scripEntity.setTradingSymbol(scrips.getTradingSymbol());
					scripEntity.setTrailingStopLoss(scrips.getTrailingStopLoss());
					scripEntity.setTransType(scrips.getTransType());
					scripEntity.setTriggerPrice(scrips.getTriggerPrice());
					scripEntity.setWeekTag(scrips.getWeekTag());
					if (StringUtil.isNotNullOrEmpty(scrips.getValidityDays())) {
						scripEntity.setValidityDays(scrips.getValidityDays());
					}
					if (StringUtil.isNotNullOrEmpty(scrips.getExpiryDate())) {
						scripEntity.setExpiryDate(scrips.getExpiryDate());
					}
					scripList.add(scripEntity);
				}
				List<BasketScripEntity> savedEntity = basketScripRepository.saveAll(scripList);
				if (!savedEntity.isEmpty() && pReq.getPushNotification() == 1) {
					List<DeviceMappingEntity> deviceDetails = deviceMappingRepository.findByUserId(userId);
					if (StringUtil.isListNotNullOrEmpty(deviceDetails)) {
						SendNoficationReqModel reqModel = new SendNoficationReqModel();
						reqModel.setMessage(pReq.getMessage());
						reqModel.setMessageType("Basket");
						reqModel.setTitle(pReq.getTitle());
						String notifyId = codifiUtil.generateNotifyId();
						reqModel.setNotifyId(notifyId);
						String[] usrId = { userId };
						reqModel.setUserId(usrId);
						sendRecommendationMsg(deviceDetails, reqModel, basketNameEntity.getBasketId());
						SendNoficationReqModel saveReqModel = new SendNoficationReqModel();
						saveReqModel.setMessage(pReq.getMessage());
						saveReqModel.setTitle(pReq.getBasketName() + " " + current_date);
						saveReqModel.setMessageType("ResearchCall");
						saveReqModel.setUserId(usrId);
						saveReqModel.setUserType("individual");
						userNotificationSpec.saveNotification(saveReqModel);
						prepareToInsertNotificationReport(pReq.getApiKey(), pReq.getBasketName(), notifyId, userId,
								basketNameEntity.getBasketId());
					} else {
						Log.info("device id is empty  for - " + userId);
					}
				}
			}
			return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to prepare and insert notification report into database
	 * 
	 * @author SowmiyaThangaraj
	 * @param pReq
	 * @param notifyId
	 */
	private void prepareToInsertNotificationReport(String apiKey, String basketName, String notifyId, String userId,
			long basketId) {
		try {
			NotificationReportEntity report = new NotificationReportEntity();
			VendorAppEntity vendor = entityManager.findByApiKey(apiKey);
			report.setVendor(vendor.getAppName());
			report.setAppCode(apiKey);
			report.setBasketId((int) basketId);
			report.setBasketName(basketName);
			report.setUserId(userId);
			report.setNotifyId(notifyId);
			report.setActiveStatus(1);
			notificationReportRepo.save(report);

		} catch (Exception e) {
			Log.error("prepareToInsertNotificationReport - ", e);
			e.printStackTrace();
		}

	}

	/**
	 * Method to validate Tpp Authorized Vendor
	 * 
	 * @author Gowthaman
	 * @param apiKey
	 * @return
	 */
	public String validateTppAuthorizedVendor(String apiKey) {
		try {
//			VendorAppEntity validVendor = vendorAppRepository.findByApiKey(apiKey);
			System.out.println("apiKey----------------------" + apiKey);
			VendorAppEntity validVendor = entityManager.findByApiKey(apiKey);
			if (validVendor != null) {
				System.out.println("TppAuthorization----------------------" + validVendor.getTppAuthorization());
				if (validVendor.getTppAuthorization() == 1) {
					return AppConstants.VALID_VENDOR;
				} else {
					return AppConstants.NOT_AUTHORIZED_BY_ADMIN;
				}
			} else {
				return AppConstants.NOT_A_VENDOR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("validateTppAuthorizedVendor -- " + e.getMessage());
		}
		return AppConstants.FAILED_STATUS;
	}

//	@Override
	public RestResponse<GenericResponse> addBasketScrip(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		try {
			/** Validate the request **/
			if (!validateAddBasketReq(basketOrderReq))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			/** Method to validate basketId **/
			if (!getValiedBasket(basketOrderReq.getBasketId(), info.getUserId()))
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_BASKET);
			/** Prepare scrip entity **/
			BasketScripEntity basketScripEntity = prepareBasketScripEntity(basketOrderReq, info);
			BasketScripEntity saveBasketScrip = basketScripRepository.saveAndFlush(basketScripEntity);
			if (saveBasketScrip != null)
				/** Return latest data **/
				return prepareResponse.prepareSuccessResponseObject(getBasketScrips(basketOrderReq.getBasketId()));
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to validate Basket Scrip
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean validateAddBasketReq(BasketOrderReq basketOrder) {
		try {
			if (basketOrder.getBasketId() > 0 && StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getExchange())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getToken())
//					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getTradingSymbol())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getQty())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPrice())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getProduct())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getTransType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getPriceType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getOrderType())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getRet())
					&& StringUtil.isNotNullOrEmpty(basketOrder.getScrips().getSource())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * method to validate the Basket Id and userId are match
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public boolean getValiedBasket(long basketId, String userId) {
		BasketNameEntity basketNameEntity = new BasketNameEntity();
		try {
			basketNameEntity = basketOrderRepository.findValiedBasketId(basketId, userId);
			if (basketNameEntity != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return false;
	}

	/**
	 * method to Map Basket Scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public BasketScripEntity prepareBasketScripEntity(BasketOrderReq basketOrderReq, ClinetInfoModel info) {
		BasketScripEntity basketScripEntity = new BasketScripEntity();
		try {
			ContractMasterModel contractMasterModel = appUtil.getContractInfo(basketOrderReq.getScrips().getExchange(),
					basketOrderReq.getScrips().getToken());
			if (contractMasterModel != null) {
				basketScripEntity.setExchange(contractMasterModel.getExch());
				basketScripEntity.setExpiry(contractMasterModel.getExpiry());
				basketScripEntity.setToken(contractMasterModel.getToken());
				basketScripEntity.setTradingSymbol(contractMasterModel.getTradingSymbol());
				basketScripEntity.setFormattedInsName(contractMasterModel.getFormattedInsName());
				basketScripEntity.setWeekTag(contractMasterModel.getWeekTag());

				basketScripEntity.setBasketId(basketOrderReq.getBasketId());
				basketScripEntity.setCreatedBy(info.getUserId());
				basketScripEntity.setDisClosedQty(basketOrderReq.getScrips().getDisClosedQty());
				basketScripEntity.setMktProtection(basketOrderReq.getScrips().getMktProtection());
				basketScripEntity.setOrderType(basketOrderReq.getScrips().getOrderType());
				basketScripEntity.setPrice(basketOrderReq.getScrips().getPrice());
				basketScripEntity.setPriceType(basketOrderReq.getScrips().getPriceType());
				basketScripEntity.setProduct(basketOrderReq.getScrips().getProduct());
				basketScripEntity.setQty(basketOrderReq.getScrips().getQty());
				basketScripEntity.setRet(basketOrderReq.getScrips().getRet());
				basketScripEntity.setSortOrder(basketOrderReq.getScrips().getSortOrder());
				basketScripEntity.setStopLoss(basketOrderReq.getScrips().getStopLoss());
				basketScripEntity.setTarget(basketOrderReq.getScrips().getTarget());
				basketScripEntity.setTrailingStopLoss(basketOrderReq.getScrips().getTrailingStopLoss());
				basketScripEntity.setTransType(basketOrderReq.getScrips().getTransType());
				basketScripEntity.setTriggerPrice(basketOrderReq.getScrips().getTriggerPrice());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		return basketScripEntity;
	}

	/**
	 * method to get basket scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	private List<BasketScripEntity> getBasketScrips(long basketId) {
		List<BasketScripEntity> basketScripEntity = new ArrayList<>();
		try {
			basketScripEntity = basketScripRepository.findByBasketId(basketId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return basketScripEntity;
	}

	/**
	 * Method to send recommendation message
	 * 
	 * @author Gowthaman
	 * @param deviceMapping
	 * @param reqModel
	 * @param basketId
	 */
	public void sendRecommendationMsg(List<DeviceMappingEntity> deviceMapping, SendNoficationReqModel reqModel,
			long basketId) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					int count = 0;
					List<String> deviceIds = new ArrayList<>();
					for (DeviceMappingEntity entity : deviceMapping) {
						String mobileDeviceId = entity.getDeviceId();
						if (StringUtil.isNotNullOrEmpty(mobileDeviceId)) {
							deviceIds.add(mobileDeviceId.trim());
						}
						if (count == 500) {
							pushNoficationUtils.sendNofification(deviceIds, reqModel, basketId);
							deviceIds = new ArrayList<String>();
							count = 0;
						}

						count++;
					}
					if (count > 0) {
						pushNoficationUtils.sendNofification(deviceIds, reqModel, basketId);
						count = 0;
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.shutdown();
				}
			}
		});
	}

	/**
	 * method to get basket id
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public long getMaxBasketId() {
		long bId = 1;
		try {
			List<BasketNameEntity> basketNameEntity = basketOrderRepository.findAll();
			if (StringUtil.isListNotNullOrEmpty(basketNameEntity)) {
				Optional<BasketNameEntity> getBasketId = basketNameEntity.stream()
						.max(Comparator.comparing(BasketNameEntity::getBasketId));
				BasketNameEntity maxBasketId = getBasketId.get();
				bId = maxBasketId.getBasketId() + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return bId;
	}

	/**
	 * Method to delete Expired Basket created
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public RestResponse<GenericResponse> deleteExpiredBasket() {
		try {
			List<Long> expiredBasketIds = basketOrderRepository.getExpiredBasketId();
			System.out.println(expiredBasketIds);
			if (StringUtil.isListNotNullOrEmpty(expiredBasketIds)) {
				for (Long id : expiredBasketIds) {
					long deleteExpiredBasketIds = basketOrderRepository.deleteByBasketId(id);
				}
				return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("deleteExpiredBasket -- " + e.getMessage());
		}

		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

	/**
	 * method to save notification action status
	 * 
	 * @author SowmiyaThangaraj
	 */
	@Override
	public RestResponse<GenericResponse> saveNotificationActionStatus(String notifyId) {
		try {
			if (StringUtil.isNotNullOrEmpty(notifyId)) {
				NotificationReportEntity userNotify = notificationReportRepo.findByNotifyId(notifyId);
				if (userNotify != null) {
					userNotify.setClickedStatus("true");
					notificationReportRepo.save(userNotify);
					return prepareResponse.prepareSuccessMessage(AppConstants.SUCCESS_STATUS);
				} else {
					return prepareResponse.prepareFailedResponse(AppConstants.NO_RECORD_FOUND + " - " + notifyId);
				}
			} else {
				return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
	}

}
