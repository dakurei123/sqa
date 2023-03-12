package com.example.sqa.utils;

import com.example.sqa.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;


@Component
public class JwtUtils implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long TOKEN_EXPIRE_TIME_ACCESS_TOKEN = 10 * 24 * 60 * 60 * 60;


    public static String getUsernameFromToken(String token, String secretKey) {
        return getClaimFromToken(token, secretKey, Claims::getSubject);
    }

    public static Date getExpirationDateFromToken(String token, String secretKey) {
        return getClaimFromToken(token, secretKey, Claims::getExpiration);
    }

    public static <T> T getClaimFromToken(String token, String secretKey, Function<Claims, T> claimsResolver) {
        final Claims claims;
        if (secretKey == null)
            claims = getAllClaimsFromToken(token);
        else
            claims = getAllClaimsFromToken(token, secretKey);

        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token, String secretKey) throws RuntimeException {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public static String getRole(String token, String secretKey) throws RuntimeException {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role");
    }

    private static Claims getAllClaimsFromToken(String token) throws RuntimeException {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        Claims claims = (Claims) jwt.getBody();
        return claims;
    }

    //check if the token has expired
    private static Boolean isTokenExpired(String token, String secretKey) {
        final Date expiration = getExpirationDateFromToken(token, secretKey);
        return expiration.before(new Date());
    }

    public static String generateAccessToken(Account accountDto, String secretKey, Long expireTime) {
        Map<String, Object> claims = new HashMap<>();
        if (accountDto != null) {
            claims.put("userId", accountDto.getId());
            claims.put("role", accountDto.getRole());
        }
        return doGenerateToken(claims, accountDto.getUsername(), secretKey, expireTime);
    }

    private static String doGenerateToken(Map<String, Object> claims, String subject, String secretKey, Long expireTime) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    public static Boolean validateToken(String token, String secretKey, UserDetails userDetails) {
        final String username = getUsernameFromToken(token, secretKey);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey));
    }

}
