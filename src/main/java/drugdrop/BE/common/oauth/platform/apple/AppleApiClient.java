package drugdrop.BE.common.oauth.platform.apple;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.common.oauth.OAuthApiClient;
import drugdrop.BE.common.oauth.OAuthLoginParams;
import drugdrop.BE.common.oauth.OAuthProvider;
import drugdrop.BE.common.oauth.dto.OAuthUserProfile;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleApiClient implements OAuthApiClient { // Apple 로그인 토큰 받기 & 사용자 정보 가져오기

    private final StringRedisTemplate tokenRedisTemplate;

    private static final String GRANT_TYPE = "authorization_code";

    private String quitUrl = "https://appleid.apple.com/auth/revoke";
    private String tokenUrl = "https://appleid.apple.com/auth/token";

    @Value("${apple.client-id}")
    private String clientId; // bundle id

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.team-id}")
    private String teamId;

    private final RestTemplate restTemplate; // 외부 요청 후 미리 정의해둔 AppleTokens, AppleInfoResponse 로 응답값을 받는다

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.APPLE;
    }

    @Override
    public AppleInfoResponse requestAccessToken(OAuthLoginParams params) {
        AppleInfoResponse ret = new AppleInfoResponse("", "");
        return ret;
    }

    public String getRefreshTokenFromCode(String code)  {
        // HELP : https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens

        String url = tokenUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", getClientSecret());
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<AppleTokenResponse> response = restTemplate.exchange(url, HttpMethod.POST,
                request, AppleTokenResponse.class);

        if(response.getStatusCode() != HttpStatus.OK){
            log.info(response.getBody().toString());
            throw new CustomException(ErrorCode.QUIT_ERROR);
        }
        return response.getBody().getRefreshToken();
    }

    public void quit(String refreshToken)  { // Apple 에서 발급한 accessToken revoke 신청
        String url = quitUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", getClientSecret());
        body.add("token", refreshToken);
        body.add("token_type_hint", "refresh_token");

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request,String.class);
        if(response.getStatusCode() != HttpStatus.OK){
            throw new CustomException(ErrorCode.QUIT_ERROR);
        }
    }

    @Override
    public OAuthUserProfile getUserInfo(String accessToken){
        return new OAuthUserProfile();
    }

    private String getClientSecret() {
        if(tokenRedisTemplate.hasKey("@APPLE")){
            return tokenRedisTemplate.opsForValue().get("@APPLE");
        }

        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        String token = null;
        try {
            token = Jwts.builder()
                    .setHeaderParam("kid", keyId)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(teamId)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expirationDate)
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                    .compact();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }

        tokenRedisTemplate.opsForValue().set("@APPLE", token);
        return token;
    }

    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource("p8_key.txt");
        String privateKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}
