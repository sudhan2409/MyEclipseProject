package in.codifi.basket.config;

import java.io.FileInputStream;

import javax.enterprise.context.ApplicationScoped;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.quarkus.logging.Log;

@ApplicationScoped
public class FirebaseConfig {

	public void initializeFirebaseApp(String name) {
		try {
			String credentialsPath = "";
			if (name.equalsIgnoreCase("android")) {
				credentialsPath = "/opt/fcm/service-account.json";
			//	credentialsPath = "D:\\service-account.json";
			} else {
				credentialsPath = "/opt/fcm/service-account.json";
				//credentialsPath = "D:\\service-account.json";
			}
			FileInputStream serviceAccount;

			serviceAccount = new FileInputStream(credentialsPath);

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			if (FirebaseApp.getApps().stream().noneMatch(app -> app.getName().equals(name))) {
				Log.info("FirebaseConfig initiated for - " + name);
				FirebaseApp.initializeApp(options, name);
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}

	public void terminateFirebaseApp(String name) {
		FirebaseApp app = FirebaseApp.getInstance(name);
		if (app != null) {
			app.delete();
		}
	}
}
