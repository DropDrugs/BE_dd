package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.Member;
import drugdrop.BE.domain.Notification;
import drugdrop.BE.domain.PointTransaction;
import drugdrop.BE.domain.TransactionType;
import drugdrop.BE.dto.response.MonthlyDisposalCountResponse;
import drugdrop.BE.dto.response.PointResponse;
import drugdrop.BE.dto.response.PointTransactionDetailResponse;
import drugdrop.BE.dto.response.PointTransactionResponse;
import drugdrop.BE.repository.MemberRepository;
import drugdrop.BE.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PointService {

    private final MemberRepository memberRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final NotificationService notificationService;

    public PointResponse getTotalPoint(Long memberId){
        Member member = getMemberOrThrow(memberId);
        return PointResponse.builder()
                .point(member.getPoint())
                .build();
    }

    public void addPoint(Long memberId, Integer point, String type){
        Member member = getMemberOrThrow(memberId);
        member.addPoint(point);
        memberRepository.save(member);
        recordPointTransaction(member, TransactionType.valueOf(type), point);
        switch(type){
            case "PHOTO_CERTIFICATION" :
                sendNotification(member, "폐기사진 인증 리워드 적립", "\uD83E\uDD17");
                break;
            case "GENERAL_CERTIFICATION" :
                sendNotification(member, "폐기 일반 인증 리워드 적립", "\uD83E\uDD17"); //🤗
                break;
            case "LOCATION_INQUIRY" :
                sendNotification(member, "폐기 장소 문의 리워드 적립", "\uD83E\uDD17");
                break;
        }
    }

    public PointTransactionResponse getPointTransactionHistory(Long memberId){
        Member member = getMemberOrThrow(memberId);
        List<PointTransaction> transactions = pointTransactionRepository.findAllByMemberId(memberId);
        List<PointTransactionDetailResponse> details = transactions.stream()
                .map(transaction -> PointTransactionDetailResponse.builder()
                        .type(transaction.getType().toString())
                        .point(transaction.getPoint())
                        .date(transaction.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
        PointTransactionResponse response = PointTransactionResponse.builder()
                .totalPoint(member.getPoint())
                .pointHistory(details)
                .build();
        return response;
    }

    public List<MonthlyDisposalCountResponse> getMonthlyDisposalStats(Long memberId){
        Member member = getMemberOrThrow(memberId);
        LocalDateTime startDate = LocalDate.now().minusYears(1).atStartOfDay(); // 현재 날짜로부터 1년 전
        List<TransactionType> disposalTypes = List.of(
                TransactionType.PHOTO_CERTIFICATION,
                TransactionType.GENERAL_CERTIFICATION);
        return pointTransactionRepository.findMonthlyDisposalCountByMemberId(memberId, disposalTypes, startDate);

    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public void recordPointTransaction(Member member, TransactionType type, Integer point){
        PointTransaction transaction = PointTransaction.builder()
                .member(member)
                .type(type)
                .point(point)
                .build();
        pointTransactionRepository.save(transaction);
    }

    private void sendNotification(Member member, String title, String message){
        if(!member.getNotificationSetting().isReward()) return;
        Notification notification = Notification.builder()
                .member(member)
                .title(title)
                .message(message)
                .build();
        notificationService.makeNotification(notification);
    }
}
