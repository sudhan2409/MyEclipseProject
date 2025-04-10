package in.codifi.basket.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
@Getter
@Setter
public class ApplicationProperties {

	// Push notification
	@ConfigProperty(name = "appconfig.push.fcm.baseurl")
	private String fcmBaseUrl;
	@ConfigProperty(name = "appconfig.push.fcm.apikey")
	private String fcmApiKey;

}
