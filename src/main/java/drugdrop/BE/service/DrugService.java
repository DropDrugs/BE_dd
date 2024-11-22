package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.Drug;
import drugdrop.BE.domain.Member;
import drugdrop.BE.dto.request.DrugDeleteRequest;
import drugdrop.BE.dto.request.DrugSaveRequest;
import drugdrop.BE.dto.response.DrugResponse;
import drugdrop.BE.repository.DrugRepository;
import drugdrop.BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DrugService {

    private final DrugRepository drugRepository;
    private final MemberRepository memberRepository;

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

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}