package in.codifi.basket.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.controller.spec.IResearchCallApiControllerSpec;
import in.codifi.basket.model.request.ResearchCallRequest;
import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.service.spec.IResearchCallApiServiceSpec;
import in.codifi.basket.utils.AppConstants;
import in.codifi.basket.utils.AppUtil;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.utils.StringUtil;
import in.codifi.cache.model.ClinetInfoModel;
import io.quarkus.logging.Log;

@Path("/research")
public class IResearchCallApiController implements IResearchCallApiControllerSpec {
	@Inject
	IResearchCallApiServiceSpec service;
	@Inject
	PrepareResponse prepareResponse;
	@Inject
	AppUtil appUtil;

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
	@Override
	public RestResponse<GenericResponse> getResWithBasketDetails(ResearchCallRequest pReq) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
		return service.getResWithBasketDetails(pReq, info.getUserId());
	}

	/**
	 *
	 * Method to retrieve researchcall detailes
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	@Override
	public RestResponse<GenericResponse> getResearchCall(ResearchCallRequest pReq) {
		ClinetInfoModel info = appUtil.getClientInfo();
		if (info == null || StringUtil.isNullOrEmpty(info.getUserId())) {
			Log.error("Client info is null");
			return prepareResponse.prepareFailedResponse(AppConstants.FAILED_STATUS);
		}
//		ClinetInfoModel info = new ClinetInfoModel();
//		info.setUserId("3030036");
		return service.getResearchCall(info.getUserId());
	}

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
		return service.getUniqStatus();
	}
}
