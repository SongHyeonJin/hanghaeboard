package com.sparta.hanghaememo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String accessToken;
    @Column(nullable = false)
    private String refreshToken;

    public Token(String username, String accessToken, String refreshToken){
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void update(Token token){
        this.refreshToken = token.getRefreshToken();
        this.accessToken = token.getAccessToken();
        this.username = token.getUsername();
    }
}