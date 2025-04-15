package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    // TODO Add id as subject and more relevant claims like company and scopes

    AccessUserDetails apiUser = (AccessUserDetails) authentication.getPrincipal();

    System.out.println("Auth name: " + authentication.getName());
    System.out.println("Company name: " + apiUser.getCompany());

    // TODO, Get the company ID or the whole object
    return Jwts.builder()
            .setSubject(authentication.getName())
            .claim("company", apiUser.getCompany())
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
            .getSubject().toString();
  }


  public UserDetails verifyokenAndGetUserDetails(String token) throws JwtException, IllegalArgumentException{
 // TODO cleanup and chagne the usage of company name to company id!
    System.out.println("Verifying token!");

    Claims jws = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getBody();


    System.out.println(jws);

    String apiKeyId = jws.getSubject().toString();
    String companyId = jws.get("company", String.class);
    System.out.println(companyId);
    List<?> scopeList = jws.get("scope", List.class);

    System.out.println("Grant auth");

    List<SimpleGrantedAuthority> authorities = scopeList.stream()
            .filter(item -> item instanceof Map)
            .map(item -> {
              @SuppressWarnings("unchecked")
              Map<String, Object> authorityMap = (Map<String, Object>) item;
              String authority = (String) authorityMap.get("authority");
              return new SimpleGrantedAuthority(authority);
            })
            .collect(Collectors.toList());

    System.out.println("VerifyToken: " + apiKeyId + " " + companyId + " " + scopeList);

    UserDetails userDetails = new JwtUserDetails(apiKeyId, companyId, authorities);

    return userDetails;
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

  public Claims extractAllClaims(String token) {
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
