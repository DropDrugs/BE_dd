package drugdrop.BE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseAuthLoginRequest {
    private String fcmToken; // for messaging
    private String idToken; // Firebase Auth ID Token
}
