package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.*;
import drugdrop.BE.dto.request.AddPointRequest;
import drugdrop.BE.dto.response.*;
import drugdrop.BE.repository.LocationBadgeRepository;
import drugdrop.BE.repository.MemberRepository;
import drugdrop.BE.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final LocationBadgeRepository locationBadgeRepository;
    private final String[] options = {"reward", "disposal"};

    public PointResponse getTotalPoint(Long memberId){
        Member member = getMemberOrThrow(memberId);
        return PointResponse.builder()
                .point(member.getPoint())
                .build();
    }

    public BadgeEarnedResponse addPoint(Long memberId, AddPointRequest request){
        String type = request.getType();
        Integer point = request.getPoint();
        String location = request.getLocation();

        Member member = getMemberOrThrow(memberId);
        member.addPoint(point);
        memberRepository.save(member);
        recordPointTransaction(member, TransactionType.valueOf(type), point, location);
        switch(type){
            case "PHOTO_CERTIFICATION" :
                sendNotification(options[0], member, "폐기사진 인증 리워드 적립", "\uD83E\uDD17");
                break;
            case "GENERAL_CERTIFICATION" :
                sendNotification(options[0], member, "폐기 일반 인증 리워드 적립", "\uD83E\uDD17"); //🤗
                break;
            case "LOCATION_INQUIRY" :
                sendNotification(options[0], member, "폐기 장소 문의 리워드 적립", "\uD83E\uDD17");
                break;
        }
        Boolean getBadge = checkLocationBadge(member, location);
        return BadgeEarnedResponse.builder().getBadge(getBadge).build();
    }

    private boolean checkLocationBadge(Member member, String location){
        if(10 == pointTransactionRepository.countByMemberIdAndLocation(member.getId(), location)) {
            LocationBadge badge = LocationBadge.builder()
                    .member(member)
                    .location(location)
                    .build();
            locationBadgeRepository.save(badge);
            return true;
        }
        return false;
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

    public void recordPointTransaction(Member member, TransactionType type, Integer point, String location){
        PointTransaction transaction = PointTransaction.builder()
                .member(member)
                .type(type)
                .point(point)
                .location(location)
                .build();
        pointTransactionRepository.save(transaction);
    }

    @Scheduled(cron="0 0 9 * * *", zone = "Asia/Seoul")
    public void sendDisposalReminderNotification(){
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);

        List<PointTransaction> transactions = pointTransactionRepository.findLatestTransactionsForMembers(
                List.of(TransactionType.PHOTO_CERTIFICATION.toString()
                        , TransactionType.GENERAL_CERTIFICATION.toString()));
        for(PointTransaction t : transactions){
            if (t.getCreatedDate().toLocalDate().isBefore(ninetyDaysAgo)) {
                sendNotification(options[1], t.getMember(), "폐기 리마인드 알림",
            "90일 전에 폐의약품을 폐기하셨습니다. 혹시 버릴 약이 있다면, 적절히 폐기할 수 있도록 확인해 주세요!");
            }
        }
    }

    private void sendNotification(String option, Member member, String title, String message){
        if(option.equals(options[0]) && !member.getNotificationSetting().isReward()) return;
        if(option.equals(options[1]) && !member.getNotificationSetting().isDisposal()) return;
        Notification notification = Notification.builder()
                .member(member)
                .title(title)
                .message(message)
                .build();
        notificationService.makeNotification(notification);
    }
}
