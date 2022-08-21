package com.silva.marcos.fluxjwt.config;

import com.silva.marcos.fluxjwt.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.function.Function;

@Component
public class JwtUtils implements Serializable {

    private final String MY_SECRET = "0d202732f92a5d58fd96bd80601780bc";

    public String getUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String genToken(User user) {
        Key mySecret = this.getSigningKey();
        return Jwts.builder().setSubject(user.getUsername()).signWith(mySecret, SignatureAlgorithm.HS256).compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        // https://github.com/jwtk/jjwt#jws-key-create
        Key mySecret = this.getSigningKey();

        return Jwts.parserBuilder().requireAudience("string").setSigningKey(mySecret).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        String s = Base64.getEncoder().encodeToString(this.MY_SECRET.getBytes());
        return Keys.hmacShaKeyFor(s.getBytes());
    }
}
