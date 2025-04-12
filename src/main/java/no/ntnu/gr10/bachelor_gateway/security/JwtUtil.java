package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for managing JSON Web Tokens (JWT) in the application.
 * Provides methods to generate, parse, and validate JWTs used for authentication and authorization.
 *
 * @author Daniel Neset
 * @version 12.04.2025
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret_key}")
  private String secretKey;

  /**
   * Generates a JWT for a given user.
   *
   * @param userDetails The user details for which the token is to be generated.
   * @return Return a signed JWT string.
   */
  public String generateToken(Authentication authentication) throws InvalidKeyException {
    final long timeNow = System.currentTimeMillis();
    final long millisecondsInHour = 60 * 60 * 1000;
    final long timeAfterOneHour = timeNow + millisecondsInHour;

    // TODO Add id as subject and more relevant claims like company and scopes

    return Jwts.builder()
            .setSubject(authentication.getName())
            .claim("username", authentication.getName())
            .claim("scope", authentication.getAuthorities())
            .setIssuedAt(new Date(timeNow))
            .setExpiration(new Date(timeAfterOneHour))
            .signWith(getSigningKey())
            .compact();
  }

  public String verifyTokenAndGetUsername(String token) throws JwtException, IllegalArgumentException {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("username", String.class);
  }

  public boolean validateToken(String token, UserDetails userDetails) throws JwtException {
    final String username = extractUsername(token);
    return userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private SecretKey getSigningKey(){
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    Jwt<JwsHeader, Claims> jws = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
    return jws.getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

}
