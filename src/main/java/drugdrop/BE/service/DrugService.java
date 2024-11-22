package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.Drug;
import drugdrop.BE.domain.Member;
import drugdrop.BE.domain.Notification;
import drugdrop.BE.dto.request.DrugDeleteRequest;
import drugdrop.BE.dto.request.DrugSaveRequest;
import drugdrop.BE.dto.response.DrugResponse;
import drugdrop.BE.repository.DrugRepository;
import drugdrop.BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DrugService {

    private final DrugRepository drugRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final String[] options = {"takeDrug", "lastIntake"};

    public List<DrugResponse> getDrugs(Long memberId){
        getMemberOrThrow(memberId);
        List<Drug> drugs = drugRepository.findAllByMemberId(memberId);
        List<DrugResponse> response = drugs.stream().map(d -> DrugResponse.builder()
                        .id(d.getId())
                        .date(d.getDate())
                        .count(d.getCount())
                        .build())
                .collect(Collectors.toList());
        return response;
    }

    public Long addDrugs(DrugSaveRequest request, Long memberId){
        Member member = getMemberOrThrow(memberId);
        Drug drug = Drug.builder()
                .member(member)
                .date(request.getDate())
                .count(request.getCount())
                .build();
        return drugRepository.save(drug).getId();
    }

    public void deleteDrugs(DrugDeleteRequest request, Long memberId){
        getMemberOrThrow(memberId);
        for(int id: request.getId()){
            try {
                drugRepository.deleteById((long) id);
            } catch (Exception e) {
                log.error(e.toString());
                throw new CustomException(ErrorCode.NOT_FOUND_DRUG);
            }
        }
    }

    @Scheduled(cron="0 0 9 * * *", zone = "Asia/Seoul")
    public void sendTakeDrugMorningNotification(){
        List<Drug> drugs = drugRepository.findAll();
        for(Drug drug : drugs){
            if(!drug.getDate().plusDays(drug.getCount()-1).isBefore(LocalDate.now()))
                sendNotification(options[0],drug.getMember(), "약 복용 알림 (아침)", "약 드실 시간이에요!");
        }
    }

    @Scheduled(cron="0 0 12 * * *", zone = "Asia/Seoul")
    public void sendTakeDrugLunchNotification(){
        List<Drug> drugs = drugRepository.findAll();
        for(Drug drug : drugs){
            if(!drug.getDate().plusDays(drug.getCount()-1).isBefore(LocalDate.now()))
                sendNotification(options[0],drug.getMember(), "약 복용 알림 (점심)", "약 드실 시간이에요!");
        }
    }

    @Scheduled(cron="0 0 18 * * *", zone = "Asia/Seoul")
    public void sendTakeDrugDinnerNotification(){
        List<Drug> drugs = drugRepository.findAll();
        for(Drug drug : drugs){
            if(drug.getDate().plusDays(drug.getCount()-1).isEqual(LocalDate.now())){
                sendNotification(options[1], drug.getMember(), "약 복용 마지막 날 알림",
                        "약을 모두 드셨으면 약을 삭제해주세요.");
            }
            if(!drug.getDate().plusDays(drug.getCount()-1).isBefore(LocalDate.now())) {
                sendNotification(options[0],drug.getMember(), "약 복용 알림 (저녁)", "약 드실 시간이에요!");
            }
        }
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private void sendNotification(String option, Member member, String title, String message){
        if(option.equals(options[0]) && !member.getNotificationSetting().isTakeDrug()) return;
        else if(option.equals(options[1]) && !member.getNotificationSetting().isLastIntake()) return;
        Notification notification = Notification.builder()
                .member(member)
                .title(title)
                .message(message)
                .build();
        notificationService.makeNotification(notification);
    }
}