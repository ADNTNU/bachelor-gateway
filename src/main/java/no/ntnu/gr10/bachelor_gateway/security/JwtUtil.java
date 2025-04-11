package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret_key}")
  private String secretKey;

  public String generateToken(UserDetails userDetails){
    final long timeNow = System.currentTimeMillis();
    final long millisecondsInHour = 60 * 60 * 1000;
    final long timeAfterOneHour = timeNow + millisecondsInHour;

    return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("scopes", "Data")
            .setIssuedAt(new Date(timeNow))
            .setExpiration(new Date(timeAfterOneHour))
            .signWith(getSignedKey())
            .compact();
  }

  public boolean validateToken(String token, UserDetails userDetails) throws JwtException {
    final String username = extractUsername(token);
    return userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }


  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private SecretKey getSignedKey(){
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    Jwt<JwsHeader, Claims> jws = Jwts.parser().verifyWith(getSignedKey()).build().parseSignedClaims(token);
    return jws.getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

}
