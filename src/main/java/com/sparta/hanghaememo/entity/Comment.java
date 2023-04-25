package com.sparta.hanghaememo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "parentId")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    public Comment(CommentRequestDto commentRequestDto){
        this.contents =commentRequestDto.getContents();
    }

    public void update(CommentRequestDto commentRequestDto){
        this.contents=commentRequestDto.getContents();
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void addLike() {
        likeCount += 1;
    }

    public void cancelLike() {
        if (likeCount - 1 < 0) return;
        likeCount -= 1;
    }

    public void updateParent(Comment parent){
        this.parent = parent;
    }
}
