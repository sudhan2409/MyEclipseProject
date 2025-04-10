package in.codifi.basket.controller;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.controller.spec.IBasketOrderApiController;
import in.codifi.basket.model.request.AdminBasketOrderReq;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.service.spec.IBasketOrderApiService;
import in.codifi.basket.utils.AppConstants;
import in.codifi.basket.utils.AppUtil;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.utils.StringUtil;

@Path("/basketorderapi")
public class BasketOrderApiController implements IBasketOrderApiController {

	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;
	@Inject
	IBasketOrderApiService basketOrderApiService;

	/**
	 * Method to Create Basket from admin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> adminCreateBasketName(AdminBasketOrderReq pReq,
			@HeaderParam(HttpHeaders.AUTHORIZATION) String token) {
		if (StringUtil.isNullOrEmpty(token) && !token.equalsIgnoreCase(AppConstants.AUTHORIZATION_HEADER))
			return prepareResponse.prepareUnauthorizedResponse();
		if (pReq == null)
			return prepareResponse.prepareFailedResponse(AppConstants.INVALID_PARAMETER);
		return basketOrderApiService.adminCreateBasketName(pReq);
	}

	/**
	 * Method to delete Expired Basket created
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> deleteExpiredBasket() {
		return basketOrderApiService.deleteExpiredBasket();
	}

	/**
	 * method to save notification action status into database
	 * 
	 * @author SowmiyaThangaraj
	 * @return
	 */
	public RestResponse<GenericResponse> saveNotificationActionStatus(String notifyId) {
		return basketOrderApiService.saveNotificationActionStatus(notifyId);

	}

}
