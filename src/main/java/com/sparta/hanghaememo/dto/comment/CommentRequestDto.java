package com.sparta.hanghaememo.dto.comment;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long board_id;
    private Long parent_id;
    private String contents;

}
