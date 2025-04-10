package in.codifi.basket.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.model.request.AdminBasketOrderReq;
import in.codifi.basket.model.response.GenericResponse;

public interface IBasketOrderApiController {

	/**
	 * Method to Create Basket from admin
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/adminCreate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> adminCreateBasketName(AdminBasketOrderReq req,
			@HeaderParam(HttpHeaders.AUTHORIZATION) String token);

	/**
	 * Method to delete Expired Basket created
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@Path("/deleteExpiredBasket")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> deleteExpiredBasket();

	/**
	 * method to save notification action status into database
	 * 
	 * @author SowmiyaThangaraj
	 * @param notifyId
	 * @return
	 */
	@Path("/notify/status/{notifyId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	RestResponse<GenericResponse> saveNotificationActionStatus(@PathParam("notifyId") String notifyId);

}
