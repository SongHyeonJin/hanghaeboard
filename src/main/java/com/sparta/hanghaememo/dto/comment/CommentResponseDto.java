package com.sparta.hanghaememo.dto.comment;

import com.sparta.hanghaememo.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String username;
    private String contents;
    private int likeCount;
    private Long parentComment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.username=comment.getUser().getUsername();
        this.contents=comment.getContents();
        this.likeCount = comment.getLikeCount();
        if(comment.getParent() != null){
            this.parentComment = comment.getParent().getId();
        }
        this.createdAt=comment.getCreatedAt();
        this.modifiedAt=comment.getModifiedAt();
    }
}
