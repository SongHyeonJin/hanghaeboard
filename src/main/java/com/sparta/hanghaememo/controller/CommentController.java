package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import com.sparta.hanghaememo.security.UserDetailsImpl;
import com.sparta.hanghaememo.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/comments")
    public ResponseEntity createComment(@RequestBody CommentRequestDto commentRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.createComment(commentRequestDto, userDetails.getUser());
    }

    @PutMapping("/api/comments/{id}")
    public ResponseEntity updateComment(@PathVariable Long id,
                                        @RequestBody CommentRequestDto commentrequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(id, commentrequestDto, userDetails.getUser());
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity deleteComment(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.deleteComment(id, userDetails.getUser());
    }

    @PostMapping("/api/boards/{boardId}/comments/{commentId}/like")
    public ResponseEntity updatecommentLike(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateCommentLike(boardId, commentId, userDetails.getUser());
    }

}
