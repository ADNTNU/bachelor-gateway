package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    AccessUserDetails apiUser = (AccessUserDetails) authentication.getPrincipal();

    return Jwts.builder()
            .setSubject(authentication.getName())
            .claim("company", apiUser.getCompanyId())
            .claim("scope", authentication.getAuthorities()
                    .stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()))
            .setIssuedAt(new Date(timeNow))
            .setExpiration(new Date(timeAfterOneHour))
            .signWith(getSigningKey())
            .compact();
  }

  public UserDetails verifyokenAndGetUserDetails(String token) throws JwtException, IllegalArgumentException{
    Claims jws = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getBody();

    String apiKeyId = jws.getSubject().toString();
    Integer companyId = jws.get("company", Integer.class);
    List<String> scopeList = jws.get("scope", List.class);
    List<SimpleGrantedAuthority> authorities = scopeList.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    return new JwtUserDetails(apiKeyId, companyId, authorities);
  }

  private SecretKey getSigningKey(){
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
  }

}
