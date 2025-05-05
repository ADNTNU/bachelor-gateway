package no.ntnu.gr10.bachelor_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
  private static final String COMPANY_ID_CLAIM = "companyId";
  private static final String SCOPES_CLAIM = "scopes";

  /**
   * Generates a JWT for a given user.
   *
   * @param authentication The user details for which the token is to be generated.
   * @return Return a signed JWT string.
   * @throws InvalidKeyException if unable to sign with key.
   */
  public String generateToken(Authentication authentication) throws InvalidKeyException {
    final long timeNow = System.currentTimeMillis();
    final long millisecondsInHour = 3600000;
    final long timeAfterOneHour = timeNow + millisecondsInHour;

    CustomUserDetails apiUser = (CustomUserDetails) authentication.getPrincipal();

    return Jwts.builder()
            .subject(authentication.getName())
            .claim(COMPANY_ID_CLAIM, apiUser.getCompanyId())
            .claim(SCOPES_CLAIM, apiUser.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .toList())
            .issuedAt(new Date(timeNow))
            .expiration(new Date(timeAfterOneHour))
            .signWith(getSigningKey())
            .compact();
  }

  // TODO refactor
  public String generateToken(String subject, Map<String, Object> claims) {
    Date now = new Date();
    return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + 3600000))
            .signWith(getSigningKey())
            .compact();
  }


  /**
   * Verifies the given JWT token and retrieves the username from it.
   * <p>
   * This method checks the validity of the provided JWT token and extracts the username from it.
   * </p>
   *
   * @param token the JWT token to verify
   * @return the username extracted from the token
   * @throws JwtException             if the token is invalid or expired
   * @throws IllegalArgumentException if the token is null or empty
   */
  public String verifyTokenAndGetUsername(String token) throws JwtException, IllegalArgumentException {
    Claims claims = verifyTokenAndGetClaims(token);

    return claims.getSubject();
  }

  /**
   * Verifies the given JWT token and retrieves the company ID from it.
   * <p>
   * This method checks the validity of the provided JWT token and extracts the company ID from it.
   * </p>
   *
   * @param token the JWT token to verify
   * @return the company ID extracted from the token
   * @throws JwtException             if the token is invalid or expired
   * @throws IllegalArgumentException if the token is null or empty
   */
  public Integer verifyTokenAndGetCompanyId(String token) throws JwtException, IllegalArgumentException {
    Claims claims = verifyTokenAndGetClaims(token);

    return claims.get(COMPANY_ID_CLAIM, Integer.class);
  }

  /**
   * Verifies the given JWT token and retrieves the scopes from it.
   * <p>
   * This method checks the validity of the provided JWT token and extracts the scopes from it.
   * </p>
   *
   * @param token the JWT token to verify
   * @return a list of scopes extracted from the token
   * @throws JwtException             if the token is invalid or expired
   * @throws IllegalArgumentException if the token is null or empty
   */
  @SuppressWarnings("unchecked")
  public List<String> verifyTokenAndGetScopes(String token) throws JwtException, IllegalArgumentException {
    Claims claims = verifyTokenAndGetClaims(token);

    Object scopes = claims.get(SCOPES_CLAIM);
    if (scopes instanceof List<?> rawList && rawList.stream().allMatch(String.class::isInstance)) {
      return (List<String>) rawList;
    }

    throw new JwtException("Invalid or malformed scopes claim in the token");
  }

  private Claims verifyTokenAndGetClaims(String token) throws JwtException, IllegalArgumentException {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Token is null or empty");
    }

    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
  }

}
