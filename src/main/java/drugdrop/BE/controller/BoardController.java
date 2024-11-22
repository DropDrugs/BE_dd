package drugdrop.BE.controller;

import drugdrop.BE.dto.request.AddBoardRequest;
import drugdrop.BE.dto.request.BoardUpdateRequest;
import drugdrop.BE.dto.response.BoardResponse;
import drugdrop.BE.dto.response.IdResponse;
import drugdrop.BE.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> getBoard() {
        List<BoardResponse> response = boardService.getBoard();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<IdResponse> addBoard(@RequestBody AddBoardRequest request) {
        Long id = boardService.addBoard(request);
        IdResponse response = IdResponse.builder().id(id).build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("")
    public ResponseEntity<Void> updateBoard(@RequestBody BoardUpdateRequest request) {
        boardService.updateBoard(request);
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id){
        boardService.deleteBoard(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
