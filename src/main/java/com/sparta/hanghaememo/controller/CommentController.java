package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import com.sparta.hanghaememo.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/comments")
    public ResponseEntity createComment(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request){
        return commentService.createComment(commentRequestDto, request);
    }

    @PutMapping("/api/comments/{id}")
    public ResponseEntity updateComment(@PathVariable Long id,  @RequestBody CommentRequestDto commentrequestDto, HttpServletRequest request){
        return commentService.updateComment(id, commentrequestDto, request);
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity deleteComment(@PathVariable Long id, HttpServletRequest request){
        return commentService.deleteComment(id, request);
    }

}
