package in.codifi.basket.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.model.request.AdminBasketOrderReq;
import in.codifi.basket.model.response.GenericResponse;

public interface IBasketOrderApiService {

	/**
	 * Method to Create Basket from admin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> adminCreateBasketName(AdminBasketOrderReq req);

	/**
	 * Method to delete Expired Basket created
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	RestResponse<GenericResponse> deleteExpiredBasket();

	/**
	 * method to save notify action status into database
	 * 
	 * @author SowmiyaThangaraj
	 * @param notifyId
	 * @return
	 */
	RestResponse<GenericResponse> saveNotificationActionStatus(String notifyId);

}
