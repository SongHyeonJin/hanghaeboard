package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import com.sparta.hanghaememo.security.UserDetailsImpl;
import com.sparta.hanghaememo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/api/boards")
    public ResponseEntity write(@RequestBody BoardRequestDto board,
                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.write(board, userDetails.getUser());
    }


    @PutMapping("/api/boards/{id}")
    public ResponseEntity updateMemo(@PathVariable Long id,
                                     @RequestBody BoardRequestDto boardDTO,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.update(id, boardDTO, userDetails.getUser());
    }

    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity delete(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.delete(id, userDetails.getUser());
    }

    @PostMapping("/api/boards/{boardId}/like")
    public ResponseEntity updateBoardLike(@PathVariable Long boardId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.updateBoardLike(boardId, userDetails.getUser());
    }

}
