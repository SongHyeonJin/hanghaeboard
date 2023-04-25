package com.sparta.hanghaememo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NON_LOGIN(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다"),
    EXPIRED_TOKEN (HttpStatus.BAD_REQUEST, "만료된 토큰입니다"),
    INVALID_TOKEN (HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다"),
    NON_AUTHORIZATION (HttpStatus.BAD_REQUEST, "작성자만 삭제/수정할 수 있습니다."),
    NO_SAME_COMMENT_ID (HttpStatus.BAD_REQUEST, "부모댓글과 자식댓글의 게시글 번호가 일치하지 않습니다."),
    NO_COMMENT (HttpStatus.BAD_REQUEST, "해당 댓글이 존재하지 않습니다."),
    NO_BOARD (HttpStatus.BAD_REQUEST, "해당 게시글이 존재하지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다.");

    private final HttpStatus status;
    private final String message;
}
