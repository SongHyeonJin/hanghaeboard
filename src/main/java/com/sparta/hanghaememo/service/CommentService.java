package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import com.sparta.hanghaememo.dto.comment.CommentResponseDto;
import com.sparta.hanghaememo.entity.*;
import com.sparta.hanghaememo.repository.BoardRepository;
import com.sparta.hanghaememo.repository.CommentRepository;
import com.sparta.hanghaememo.repository.LikesRepository;
import com.sparta.hanghaememo.security.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;


    @Transactional
    public ResponseEntity createComment(CommentRequestDto commentRequestDto, Users user){
        Board board = existBoard(commentRequestDto.getBoard_id());
        Comment comment = new Comment(commentRequestDto);

        Comment parentComment = null;
        //자식댓글 경우
        if(commentRequestDto.getParent_id() != null) {
            // 대댓글 부모 댓글 존재여부 확인
            parentComment = existComment(commentRequestDto.getParent_id());
            // 부모댓글의 게시글 번호와 자식댓글의 게시글 번호 같은지 체크하기
            if (parentComment.getBoard().getId() != commentRequestDto.getBoard_id()) {
                throw new CustomException(ErrorCode.NO_SAME_COMMENT_ID);
            }
        }

            comment.setUser(user);
            comment.setBoard(board);

            // 부모 댓글 설정
            if(parentComment != null){
                comment.updateParent(parentComment);
                log.info("-------------parentCommentId : "+comment.getParent().getId());
            }else{
                comment.updateParent(null);
            }
            
//            List<Comment> commentList = board.getCommentList();
//            commentList.add(comment);
//            board.addComment(commentList);

            commentRepository.save(comment);
//            boardRepository.save(board);

            CommentResponseDto commentResponseDto = null;
            if(parentComment != null){
                commentResponseDto = CommentResponseDto.builder()
                        .id(comment.getId())
                        .username(comment.getUser().getUsername())
                        .contents(comment.getContents())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .parentComment(comment.getParent().getId())
                        .build();
            } else {
                commentResponseDto = CommentResponseDto.builder()
                        .id(comment.getId())
                        .username(comment.getUser().getUsername())
                        .contents(comment.getContents())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build();
            }

        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 작성 성공", commentResponseDto);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity updateComment(Long id, CommentRequestDto requestDto, Users user) {
        Comment comment = existComment(id);
        // 작성자 게시글 체크
        isCommentUsers(user,comment);
        comment.update(requestDto);
        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 수정 성공", new CommentResponseDto(comment));
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity deleteComment(Long id, Users user) {
        Comment comment = existComment(id);
        // 작성자 게시글 체크
        isCommentUsers(user,comment);
        // 좋아요 삭제
        likesRepository.deleteByCommentId(comment.getId());

        commentRepository.deleteById(id);
        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 삭제 성공", null);
        return new ResponseEntity(responseDTO , HttpStatus.OK);
    }

    @Transactional
    // 댓글 좋아요/ 좋아요 취소
    public ResponseEntity updateCommentLike(Long boardId, Long commentId, Users user){
        Board board = existBoard(boardId);
        Comment comment = existComment(commentId);
        ResponseDto responseDto;

        // 이미 좋아요가 되었으면 취소
         if(existCommentLike(user, board, comment)){
             comment.cancelLike();
             likesRepository.deleteByUsersIdAndBoardIdAndCommentId(user.getId(), boardId, commentId);
             responseDto = ResponseDto.setSuccess("댓글 좋아요 취소 성공",StatusEnum.OK);
         }else{
             // 좋아요
             comment.addLike();
             Likes likes = new Likes(user, board, comment);
             likesRepository.save(likes);
             responseDto = ResponseDto.setSuccess("댓글 좋아요 성공",StatusEnum.OK);
         }
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    private Board existBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_BOARD)
        );
        return board;
    }

    private Comment existComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_COMMENT)
        );
        return comment;
    }

    private void isCommentUsers(Users users, Comment comment) {
        if (!comment.getUser().getUsername().equals(users.getUsername()) && !users.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NON_AUTHORIZATION);
        }
    }

    //댓글 좋아요 여부 확인
    private boolean existCommentLike(Users user, Board board, Comment comment) {
        Optional<Likes> like = likesRepository.findByUsersIdAndBoardIdAndCommentId(user.getId(), board.getId(), comment.getId());
        if (like.isPresent()) {
            return true;
        }
        return false;
    }

}
