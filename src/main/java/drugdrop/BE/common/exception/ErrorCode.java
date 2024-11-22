package drugdrop.BE.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Member 예외
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    EXIST_MEMBER_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    SECURITY_CONTEXT_NOT_FOUND(HttpStatus.NOT_FOUND, "Security Context 에 인증 정보가 없습니다."),
    EXIST_MEMBER(HttpStatus.CONFLICT, "이미 가입되어 있는 유저입니다."),
    LOGOUTED_MEMBER(HttpStatus.CONFLICT, "로그아웃 된 사용자입니다."),
    EXIST_MEMBER_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    NOT_FOUND_CHARACTER(HttpStatus.NOT_FOUND, "해당 캐릭터를 보유하고 있지 않습니다."),
    NOT_ENOUGH_POINTS(HttpStatus.CONFLICT, "유저의 포인트가 부족합니다."),


    // Token 예외
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh Token을 찾을 수 없는 사용자입니다. 다시 로그인하세요."),
    UNAUTHORIZED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다."),
    ID_TOKEN_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "ID Token 값이 잘못되었습니다. OAUTH 로그인을 다시 시도해보세요."),
    QUIT_ERROR(HttpStatus.BAD_REQUEST, "OAUTH 탈퇴 과정 중 에러가 발생했습니다."),
    LOGIN_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "OAUTH 토큰 에러"),

    // FCM Messaging 예외
    FCM_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "FCM토큰이 null입니다."),
    FCM_MESSAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 메세징 오류"),

    // Map 예외
    NOT_FOUND_MAP_RESULT(HttpStatus.NOT_FOUND, "장소 검색 결과가 없습니다."),

    // Drug 예외
    NOT_FOUND_DRUG(HttpStatus.NOT_FOUND, "해당 약이 존재하지 않습니다."),

    // BOARD 예외
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "해당 공지사항 글이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
