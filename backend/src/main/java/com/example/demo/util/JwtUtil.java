package com.example.demo.util;

import com.example.demo.modelDB.User;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final String jwtSecret;
    private final int jwtExpirationMs = 86400000;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtUtil(@Value("${jwt.secret}")String jwtSecret){
        this.jwtSecret = jwtSecret;
    }

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void invalidateToken(String token){
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();

            tokenBlacklistService.blackListToken(token, expirationDate.getTime());
        }catch(Exception e){
            logger.error("Error invalidating the token: {}", e.getMessage());
        }
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getRegistrationNumberFromJwtToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken){
        try{
            if(tokenBlacklistService.isBlacklisted(authToken))
                return false;
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT Token: {}", e.getMessage());
        }catch(ExpiredJwtException e){
            logger.error("JWT Token is expired: {}", e.getMessage());
        }catch(UnsupportedJwtException e){
            logger.error("JWT Token is unsupported: {}", e.getMessage());
        }catch(IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
