package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.NotificationSetting;
import drugdrop.BE.domain.Member;
import drugdrop.BE.domain.TransactionType;
import drugdrop.BE.dto.request.NotificationSettingRequest;
import drugdrop.BE.dto.response.MemberDetailResponse;
import drugdrop.BE.dto.response.NotificationResponse;
import drugdrop.BE.dto.response.NotificationSettingResponse;
import drugdrop.BE.repository.LocationBadgeRepository;
import drugdrop.BE.repository.MemberRepository;
import drugdrop.BE.repository.NotificationRepository;
import drugdrop.BE.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final LocationBadgeRepository locationBadgeRepository;
    private final NotificationRepository notificationRepository;
    private final PointService pointService;
    private final Integer characterCost = 200;

    @Transactional(readOnly = true)
    public void checkNicknameDuplicate(String nickname) {
        checkNicknameAvailability(nickname);
    }

    // 유저 이름 변경
    public void changeMemberNickname(Long memberId, String nickname){
        Member member = getMemberOrThrow(memberId);
        checkNicknameAvailability(nickname);
        member.setNickname(nickname);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.EXIST_MEMBER_EMAIL);
        }
    }

    // 유저 정보 조회
    public MemberDetailResponse getMemberDetail(Long memberId){
        Member member = getMemberOrThrow(memberId);
        NotificationSetting n = member.getNotificationSetting();
        NotificationSettingResponse nr = NotificationSettingResponse.builder()
                .disposal(n.isDisposal())
                .reward(n.isReward())
                .noticeboard(n.isNoticeboard())
                .takeDrug(n.isTakeDrug())
                .lastIntake(n.isLastIntake())
                .build();
        List<Integer> chars = getOwnedCharacterIndices(member);
        List<String> badges = locationBadgeRepository.findAllByMemberId(memberId).stream()
                .map(b -> b.getLocation())
                .collect(Collectors.toList());
        return MemberDetailResponse.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .selectedChar(member.getSelectedChar())
                .ownedChars(chars)
                .notificationSetting(nr)
                .locationBadges(badges)
                .point(member.getPoint())
                .build();
    }

    public List<Integer> getOwnedCharacterIndices(Member member) {
        List<Integer> ownedCharsList = new ArrayList<>();
        int ownedChars = member.getOwnedChars();

        for (int i = 0; i < Integer.SIZE; i++) { // int는 32비트이므로 Integer.SIZE 사용
            if ((ownedChars & (1 << i)) != 0) {
                ownedCharsList.add(i); // i번째 비트가 1이면 리스트에 추가
            }
        }
        return ownedCharsList;
    }

    public void updateNotificationSetting(Long memberId, NotificationSettingRequest request){
        Member member = getMemberOrThrow(memberId);
        NotificationSetting setting = member.getNotificationSetting();
        setting.setDisposal(request.getDisposal());
        setting.setNoticeboard(request.getNoticeboard());
        setting.setReward(request.getReward());
        notificationSettingRepository.save(setting);
        member.updateNotificationSetting(setting);
        memberRepository.save(member);
    }

    public void changeMemberCharacter(Long memberId, Integer charId){
        Member member = getMemberOrThrow(memberId);
        if(!hasCharacter(member, charId)){
            throw new CustomException(ErrorCode.NOT_FOUND_CHARACTER);
        }
        member.setSelectedChar(charId);
        memberRepository.save(member);
    }

    public boolean hasCharacter(Member member, int charId) {
        int characterBit = 1 << charId;
        return (member.getOwnedChars() & characterBit) != 0;
    }

    public void buyMemberCharacter(Long memberId, Integer charId){
        Member member = getMemberOrThrow(memberId);
        if(member.getPoint() < characterCost){
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINTS);
        }
        member.setOwnedChars(member.getOwnedChars() | (1 << charId));
        member.subPoint(characterCost);
        memberRepository.save(member);
        pointService.recordPointTransaction(member, TransactionType.CHARACTER_PURCHASE, -200, "none");
    }

    public List<NotificationResponse> getMemberNotificationHistory(Long memberId){
        getMemberOrThrow(memberId);
        return notificationRepository.findAllByMemberId(memberId).stream()
                .map(n-> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .createdAt(n.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private void checkNicknameAvailability(String nickname){
        if(memberRepository.existsByNickname(nickname)){
            throw new CustomException(ErrorCode.EXIST_MEMBER_NICKNAME);
        }
    }
}
