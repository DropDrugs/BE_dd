package drugdrop.BE.service;

import drugdrop.BE.common.exception.CustomException;
import drugdrop.BE.common.exception.ErrorCode;
import drugdrop.BE.domain.Board;
import drugdrop.BE.domain.Member;
import drugdrop.BE.domain.Notification;
import drugdrop.BE.dto.request.AddBoardRequest;
import drugdrop.BE.dto.request.BoardUpdateRequest;
import drugdrop.BE.dto.response.BoardResponse;
import drugdrop.BE.repository.BoardRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<BoardResponse> getBoard(){
        return boardRepository.findAll().stream().map(b->BoardResponse.builder()
                        .title(b.getTitle())
                        .content(b.getContent())
                        .createdAt(b.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    public Long addBoard(AddBoardRequest request){
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        sendNotificationToAll("새 공지사항 알림", request.getTitle());
        return boardRepository.save(board).getId();
    }

    public void updateBoard(BoardUpdateRequest request){
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        board.updateBoard(request.getTitle(), request.getContent());
        boardRepository.save(board);
    }

    public void deleteBoard(Long boardId){
        try {
            boardRepository.deleteById(boardId);
        } catch (Exception e){
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD);
        }
    }

    private void sendNotificationToAll(String title, String message){
        List<Member> members = memberRepository.findAll();
        for(Member member: members){
            if(!member.getNotificationSetting().isNoticeboard()) continue;
            Notification notification = Notification.builder()
                    .member(member)
                    .title(title)
                    .message(message)
                    .build();
            notificationService.makeNotification(notification);
        }
    }
}
