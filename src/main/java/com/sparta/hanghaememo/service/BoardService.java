package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import com.sparta.hanghaememo.dto.board.BoardResponseDto;
import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.entity.*;
import com.sparta.hanghaememo.repository.BoardRepository;
import com.sparta.hanghaememo.repository.LikesRepository;
import com.sparta.hanghaememo.security.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public ResponseEntity write(BoardRequestDto boardRequestDTO , Users user){
        Board board = new Board(boardRequestDTO);

        board.addUser(user);
        boardRepository.save(board);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 작성 성공", boardRequestDTO);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    // Board 객체를 BoardResponseDTO 타입으로 변경해 리스트 반환
    public ResponseEntity list() {
        List<BoardResponseDto> boardList = boardRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 목록 조회 성공", boardList);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity listOne(Long id) {
        // 게시글 존재여부 확인
        Board board = existBoard(id);
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 조회 성공", boardResponseDto);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity update(Long id, BoardRequestDto boardRequestDTO, Users user) {
        // 게시글 존재여부 확인
        Board board = existBoard(id);

        // 작성자 게시글 체크
        isBoardUsers(user, board);

        board.update(boardRequestDTO);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 수정 성공",boardRequestDTO);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity delete(Long id, Users user) {
        // 게시글 존재여부 확인
        Board board = existBoard(id);
        // 작성자 게시글 체크
        isBoardUsers(user, board);
        // 좋아요 삭제
        likesRepository.deleteByBoardId(id);

        boardRepository.deleteById(id);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 삭제 성공",null);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity updateBoardLike(Long id, Users user){
        // 게시글 존재여부 확인
        Board board = existBoard(id);
        existBoardLike(user, board);
        ResponseDto responseDto;

        // 이미 좋아요를 했으면 취소
        if(existBoardLike(user, board)){
            board.cancelLike();
            likesRepository.deleteByUsersIdAndBoardId(user.getId(), id);
            responseDto = ResponseDto.setSuccess("게시글 좋아요 취소 성공", StatusEnum.OK);
        }else{
            // 좋아요 추가
            board.addLike();
            Likes likes = new Likes(user, board);
            likesRepository.save(likes);
            responseDto = ResponseDto.setSuccess("게시글 좋아요 성공", StatusEnum.OK);
        }
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    private Board existBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_BOARD)
        );
        return board;
    }

    private void isBoardUsers(Users users, Board board) {
        if (!board.getUser().getUsername().equals(users.getUsername()) && !users.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NON_AUTHORIZATION);
        }
    }

    // 게시글 좋아요 여부 확인
    private boolean existBoardLike(Users user, Board board){
        Optional<Likes> like = likesRepository.findByUsersIdAndBoardId(user.getId(), board.getId());
        if(like.isPresent()){
            return true;
        }
        return false;
    }

}
