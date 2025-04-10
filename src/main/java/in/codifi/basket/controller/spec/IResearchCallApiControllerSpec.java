package in.codifi.basket.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.model.request.ResearchCallRequest;
import in.codifi.basket.model.response.GenericResponse;

public interface IResearchCallApiControllerSpec {

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
	@Path("getResearchWithBasket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getResWithBasketDetails(ResearchCallRequest pReq);

	/**
	 *
	 * Method to retrieve researchcall detailes
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	@Path("/getResearchCall")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getResearchCall(ResearchCallRequest pReq);

	/**
	 * 
	 * Method to retrieve unique status
	 *
	 * @author gokul
	 *
	 * @param req
	 * @return
	 */
	@Path("/getUniqStatus")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> getUniqStatus();
}
