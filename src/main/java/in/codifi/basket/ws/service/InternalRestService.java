package in.codifi.basket.ws.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import in.codifi.basket.model.response.GenericResponse;
import in.codifi.basket.utils.PrepareResponse;
import in.codifi.basket.ws.model.OrderDetails;
import in.codifi.basket.ws.service.spec.InternalRestServiceSpec;
import io.quarkus.logging.Log;

@ApplicationScoped
public class InternalRestService {

	@Inject
	@RestClient
	InternalRestServiceSpec internalRestServiceSpec;

	@Inject
	PrepareResponse prepareResponse;

	public List<GenericResponse> executeOrders(List<OrderDetails> orderDetails, String accessToken) {
		List<GenericResponse> response = null;
		try {
			String token = "Bearer " + accessToken;
			response = internalRestServiceSpec.placeOrder(token, orderDetails);
		} catch (ClientWebApplicationException e) {
			e.printStackTrace();
			Log.error(e.getMessage());
			int statusCode = e.getResponse().getStatus();

		}
		return response;

	}
}
