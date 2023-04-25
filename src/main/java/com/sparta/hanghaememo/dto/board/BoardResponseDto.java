package com.sparta.hanghaememo.dto.board;

import com.sparta.hanghaememo.entity.Board;
import com.sparta.hanghaememo.entity.Comment;
import com.sparta.hanghaememo.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Users user;
    private List<Comment> commentList;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.likeCount = board.getLikeCount();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.commentList = board.getCommentList();
        this.user = board.getUser();
    }
}
