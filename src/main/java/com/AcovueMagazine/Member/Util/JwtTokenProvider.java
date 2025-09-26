package com.AcovueMagazine.Member.Util;

import com.AcovueMagazine.Member.Config.JwtToken;
import com.AcovueMagazine.Member.Dao.RedisDao;
import com.AcovueMagazine.Member.Dto.MemberLoginDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.time.Duration;
import java.util.Base64;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

//    private final UserDetailsService userDetailsService;
    private final RedisDao redisDao; // RefreshToken 저장을 위해 Redis 사용

    private static final String GRANT_TYPE = "Bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    // yml 파일에서 secret 값 가져와서 secretKey 설정
    public JwtTokenProvider(
            @Value("${jwt.secret}")
            String secretKey,
            RedisDao redisDao) {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.userDetailsService = userDetailsService;
        this.redisDao = redisDao;
    }

    // 인증 객체 가지고 Token(Access, Refresh) 생성하는 메서드
    public MemberLoginDto.TokenResDto generateToken(Authentication authentication){
        //권한 가져오기
        // JWT 토큰의 claims에 포함되어 사용자의 권한 정보를 저장
        String authorities = authentication.getAuthorities().stream() // Authoritication객체에서 사용자 권한 목록 가져오기
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        String username = authentication.getName();

        //AccessToken생성
        Date accessTokenExpire = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = generateAccessToken(username, authorities, accessTokenExpire);

        //RefreshToken 생성
        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(username, refreshTokenExpire);

        // Redis에 RefreshTOken을 넣기
        // 만료 되면 자동 삭제
        redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));

        return MemberLoginDto.TokenResDto.builder()
                .grantType(GRANT_TYPE) // Bearer
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    private String generateRefreshToken(String username, Date expireDate) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateAccessToken(String username, String authorities, Date expireDate) {
        return Jwts.builder()
                .setSubject(username) // 토큰 제목(사용자이름)
                .claim("auth", authorities) // 권한 정보 (커스텀 클레임)
                .setExpiration(expireDate) // 만료시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact(); // JWT 문자열 생성
    }

    // 이건 리프래쉬 토큰 재발급, 재발급 된 리프래쉬 토큰 다시 레디스에 저장하는 로직
//    public JwtToken generateTokenWithRefreshToken(String username){
//        long now = (new Date()).getTime();
//        // AccessToken 생성
//        Date accessTokenExpire = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
//        // UserDetailService로 유저 정보 가져오기
////        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        String authorities = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        String accessToken = generateAccessToken(username, authorities, accessTokenExpire);
//
//        //RefreshToken 생성
//        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
//        String refreshToken = generateRefreshToken(username, refreshTokenExpire);
//
//        // 재 발급된 RefreshToken 을 Redis에 넣기
//        redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
//
//        return JwtToken.builder()
//                .grantType(GRANT_TYPE)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken).build();
//    }

    //JWT토큰 복호화 해서 정보 꺼내기
    public Authentication getAuthentication(String accessToken) {
        //JWT 토큰 복호화
        Claims claims = parseClaims(accessToken);
        if(claims.get("auth") == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //Claim에 있는 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new) // SimpleGrantedAuthority 객체 -> 컬랙션
                .toList();

        //UserDetails 객체 만들어서 Authentication return
        //UserDetails: interface, Member: UserDetails 로 구현한 클래스
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);

    }

    // JWT 복호화
    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    // TOken 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch(SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token", e);
        } catch(ExpiredJwtException e){
            log.info("Expired JWT Token", e);
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        } catch(IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // RefreshTOken 검증
    public boolean validateRefreshToken(String token) {
        // 기본 JWT 검증
        if(!validateToken(token))return false;

        try{
            // Token에서 username 추출하기
            String username = getUserNameFromToken(token);
            // redis에 있는 token하고 바교
            String redisToken =(String) redisDao.getValues(username);
            return token.equals(redisToken);
        } catch(Exception e){
            log.info("RefreshToken Validation Failed", e);
            return false;
        }
    }

    private String getUserNameFromToken(String token) {
        try{
            // 토큰 파싱해서 클레임 얻기
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 사용자 이름 Subject 변환
            return claims.getSubject();
        } catch(ExpiredJwtException e){
            // 토큰 만되어도 클레임은 가져올 수 있음
            return e.getClaims().getSubject();
        }
    }

    //RefreshToken 삭제
    public void deleteRefreshToken(String username) {
        if(username == null || username.isEmpty()){
            throw new IllegalArgumentException("username cannot be null or empty");
        }

        // 로그아웃 시 redis.dao에서 refreshTOken 삭제
        redisDao.deleteValues(username);
    }

}
