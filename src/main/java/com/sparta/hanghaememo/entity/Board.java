package com.sparta.hanghaememo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @OrderBy("createdAt desc")
    private List<Comment> commentList;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    public Board(String title, String contents, Users user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
    }

    public Board(BoardRequestDto boardDTO){
        this.title = boardDTO.getTitle();
        this.contents = boardDTO.getContents();
    }

    public void update(BoardRequestDto boardDTO) {
        this.title = boardDTO.getTitle();
        this.contents = boardDTO.getContents();
    }


    public void addUser(Users user) {
        this.user = user;
    }

    public void addComment(List<Comment> commentList){ this.commentList = commentList; }

    public void addLike() {
        likeCount += 1;
    }

    public void cancelLike() {
        if (likeCount - 1 < 0) return;
        likeCount -= 1;
    }
}
