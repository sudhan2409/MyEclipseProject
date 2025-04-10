package in.codifi.basket.utils;

import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.basket.model.request.ResearchCallModelRequest;
import in.codifi.basket.model.response.GenericResponse;

@ApplicationScoped
public class ValidateUtil {

	@Inject
	PrepareResponse prepareResponse;
	private static final Pattern DATE_PATTERN = Pattern
			.compile("^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

	/**
	 * 
	 * Method to validate expiry date
	 *
	 * @author saranya
	 *
	 * @param pReq
	 * @return
	 */
	public RestResponse<GenericResponse> validateExpiryDate(ResearchCallModelRequest pReq) {

		if (pReq != null && StringUtil.isNotNullOrEmpty(pReq.getExpiryDate())
				&& !DATE_PATTERN.matcher(pReq.getExpiryDate()).matches()) {
			return prepareResponse.prepareFailedResponse("Invalid Date format.Date should be in the format:YYYY-MM-DD");
		}
		return null;
	}

	/**
	 * 
	 * Method to validate basket id
	 *
	 * @author saranya
	 *
	 * @param pReq
	 * @return
	 */
	public RestResponse<GenericResponse> validatebasketIdDetails(ResearchCallModelRequest pReq) {

		if (pReq == null) {
			return prepareResponse.prepareFailedResponse("Request body is missing");
		}

		// Check if BasketId is null or 0 (not provided)
		if (isNullOrEmpty(pReq.getBasketId())) {
			return prepareResponse.prepareFailedResponse("Basket id is required");
		}

		// Convert the BasketId to String and check if it contains only numbers
		if (!isNumberOnly(String.valueOf(pReq.getBasketId()))) {
			return prepareResponse.prepareFailedResponse("Basket id should be positive numeric ");
		}

		// If all validations pass, return null (indicating success)
		return null;
	}

	/**
	 * 
	 * Method to to check if a String contains only numeric characters
	 *
	 * @author saranya
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumberOnly(String str) {
		if (str == null || str.isEmpty()) {
			return false; // Return false for null or empty string
		}

		// Check if all characters in the string are digits
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false; // Found a non-numeric character
			}
		}
		return true; // Contains only numbers
	}

	/**
	 * 
	 * Method to validate null or empty using helper method
	 *
	 * @author saranya
	 *
	 * @param str
	 * @return
	 */
	// Method to handle Long values (null or 0 check)
	public static boolean isNullOrEmpty(Long str) {
		return str == null || str == 0L; // Handling Long object (null or 0)
	}

}