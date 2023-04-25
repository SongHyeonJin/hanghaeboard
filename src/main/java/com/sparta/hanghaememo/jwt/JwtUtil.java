package com.sparta.hanghaememo.jwt;

import com.sparta.hanghaememo.entity.UserRoleEnum;
import com.sparta.hanghaememo.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String ACCESS_HEADER = "ACCESS_HEADER";
    public static final String REFRESH_HEADER = "REFRESH_HEADER";

    private static final String BEARER_PREFIX = "Bearer ";
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret.key}")
    private String SECURITY_KEY;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; //HS256 암호화 알고리즘 사용

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SECURITY_KEY);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role, String token) {
        Date date = new Date();
        Date ACCESS_TIME = (Date)Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        Date REFRESH_TIME = (Date)Date.from(Instant.now().plus(7, ChronoUnit.DAYS));

        String header = token.equals("ACCESS_HEADER")? ACCESS_HEADER : REFRESH_HEADER;
        Date exprTime = token.equals("ACCESS_HEADER")? ACCESS_TIME : REFRESH_TIME ;

        //토큰 앞은 Bearer이 붙음
        //String 형식의 jwt토큰으로 반환됨
        return BEARER_PREFIX +
                Jwts.builder()
                        .signWith(SignatureAlgorithm.HS512, SECURITY_KEY)
                        .claim(header, role) //auth 키에 사용자 권한 value 담기
                        .setSubject(username) //subject라는 키에 username 넣음
                        .setExpiration(exprTime) //(현재시간 + 1시간)토큰 유효기간 지정
                        .setIssuedAt(date) //언제 토큰이 생성 되었는가
                        .signWith(key, signatureAlgorithm) //생성한 key 객체와 key객체를 어떤 알고리즘을 통해 암호화 할건지 지정
                        .compact();
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, String header) {
        String resolveHeader = header.equals("ACCESS_HEADER")? ACCESS_HEADER : REFRESH_HEADER;

        String bearerToken = request.getHeader(resolveHeader);
        //토큰 값이 있는지, 토큰 값이 Bearer 로 시작하는지 판단
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            //Bearer를 자른 값을 전달
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            //토큰 검증 (내부적으로 해준다)
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public String getUserInfoFromToken(String token) {
        // token 을 키를 사용해 복호화
        //검증 하고, token 의 payload 에서 getBody().getSubject() 를 통해서 안에 들어있는 정보를 가져옴
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
