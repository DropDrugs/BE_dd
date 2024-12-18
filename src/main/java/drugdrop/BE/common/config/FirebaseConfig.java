package drugdrop.BE.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        ClassPathResource resource = new ClassPathResource("drug-drop-firebase.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();
        if(firebaseAppList != null && !firebaseAppList.isEmpty()){
            for(FirebaseApp app : firebaseAppList) {
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } else {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
        }
        return firebaseApp;
    }

    @Bean
    public FirebaseAuth getFirebaseAuth() throws IOException {
        return FirebaseAuth.getInstance(firebaseApp());
    }

    @Bean
    public FirebaseMessaging getFirebaseMessaging() throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}
