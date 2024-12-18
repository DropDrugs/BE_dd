package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FCMTokenService {

    private final StringRedisTemplate tokenRedisTemplate;

    public void sendNotification(String title, String content, Long memberId, String extraInfo) {
        if(!hasKey(memberId)){
            log.error("FCM TOKEN INVALID ERROR");
//            throw new CustomException(ErrorCode.FCM_TOKEN_INVALID);
            return;
        }
        String token = getToken(memberId);
        Notification fcmNotification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(fcmNotification)
                .putData("memberId", String.valueOf(memberId))
                .putData("extraInfo", extraInfo)
                .build();
        sendMessage(message);
    }

    public void saveToken(Long memberId, String FCMToken){
        tokenRedisTemplate.opsForValue()
                .set("_"+String.valueOf(memberId), FCMToken);
    }

    private String getToken(Long memberId) {
        return tokenRedisTemplate.opsForValue().get("_"+String.valueOf(memberId));
    }

    public void deleteToken(Long memberId) {
        tokenRedisTemplate.delete(String.valueOf("_"+memberId));
    }

    public boolean hasKey(Long memberId){
        return tokenRedisTemplate.hasKey(String.valueOf("_"+memberId));
    }

    public void sendMessage(Message message) {
        try {
            FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            log.error("FCM MESSAGE FAILED ERROR");
//            throw new CustomException(ErrorCode.FCM_MESSAGE_FAILED);
        }
    }
}

