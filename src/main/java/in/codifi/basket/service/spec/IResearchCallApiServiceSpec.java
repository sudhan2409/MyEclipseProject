package in.codifi.basket.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.model.request.ResearchCallRequest;
import in.codifi.basket.model.response.GenericResponse;

public interface IResearchCallApiServiceSpec {

	/**
	 *
	 * Method to get Research with Basket Details
	 *
	 * @author gokul
	 *
	 * @param req
	 * @param token
	 * @return
	 */
	RestResponse<GenericResponse> getResWithBasketDetails(ResearchCallRequest pReq, String string);

	/**
	 *
	 * Method to retrieve researchcall detailes
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	RestResponse<GenericResponse> getResearchCall(String pClientId);

	/**
	 * 
	 * Method to retrieve unique status
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	RestResponse<GenericResponse> getUniqStatus();

}
