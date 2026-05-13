package com.warehouse.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public static boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            String jwtSecret = getJwtSecret();
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        } catch (Exception e) {
            throw new RuntimeException("Token无效");
        }
    }

    public static String getUsernameFromToken(String token) {
        try {
            String jwtSecret = getJwtSecret();
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        } catch (Exception e) {
            throw new RuntimeException("Token无效");
        }
    }

    private static String getJwtSecret() {
        // 优先从系统属性获取（.env 加载的），其次从环境变量获取
        String secret = System.getProperty("JWT_SECRET");
        if (!StringUtils.hasText(secret)) {
            secret = System.getenv("JWT_SECRET");
        }
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("JWT_SECRET未设置，请检查 .env 文件或环境变量");
        }
        return secret;
    }
}
