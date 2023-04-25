package com.sparta.hanghaememo.repository;

import com.sparta.hanghaememo.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUsersIdAndBoardId(Long userId, Long boardId);
    Optional<Likes> findByUsersIdAndBoardIdAndCommentId(Long userId, Long boardId, Long commentId);

    void deleteByUsersIdAndBoardId(Long userId, Long boardId);

    void deleteByUsersIdAndBoardIdAndCommentId(Long userId, Long boardId, Long commentId);

    void deleteByBoardId(Long boardId);

    void deleteByCommentId(Long commentId);
}
