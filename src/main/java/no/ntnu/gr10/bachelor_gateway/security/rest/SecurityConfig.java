package no.ntnu.gr10.bachelor_gateway.security.rest;

import no.ntnu.gr10.bachelor_gateway.security.Scopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for setting up authentication and authorization.
 * This class configures the security filter chain, authentication manager, and password encoder.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


  @Value("#{'${cors.allowedOrigins}'.split(',')}")
  private List<String> allowedOrigins;

  private final ReactiveUserDetailsService userDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Constructs a new SecurityConfig.
   *
   * @param userDetailsService the service to load user-specific data
   * @param jwtAuthenticationFilter the filter for JWT authentication
   */
  public SecurityConfig(ReactiveUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter){
    this.userDetailsService = userDetailsService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  /**
   * Configures authentication manager using the provided UserDetailsService.
   */
  @Bean
  public ReactiveAuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    UserDetailsRepositoryReactiveAuthenticationManager manager =
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    manager.setPasswordEncoder(passwordEncoder);
    return manager;
  }


  /**
   * Configures CORS (Cross-Origin Resource Sharing) settings.
   * This method sets up the allowed origins, methods, and headers for CORS requests.
   *
   * @return a CorsConfigurationSource instance.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(allowedOrigins);
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);

    return source;
  }

  /**
   * Defines the security filter chain.
   *
   * @param http the HttpSecurity to configure
   * @return the SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/auth/**").permitAll()
                    .pathMatchers("/ws-auth-token").permitAll()
                    .pathMatchers("/ws/data/**").permitAll()
                    .pathMatchers("/swagger-ui/**","/v3/api-docs/**","/webjars/**").permitAll()
                    .pathMatchers("/rest/swagger-ui/**","/rest/v3/api-docs/**").permitAll()
                    .pathMatchers("/rest/fisheryActivities/**").hasAuthority(Scopes.FISHERY_ACTIVITY.getAuthority())
                    .pathMatchers("/rest/fishingFacilities/**").hasAuthority(Scopes.FISHING_FACILITY.getAuthority())
                    .pathMatchers("/restAdm/**").hasAuthority(Scopes.ADMIN.getAuthority())
                    .anyExchange().authenticated())
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
  }

  /**
   * Returns a PasswordEncoder that performs no encoding.
   *
   * @return a PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
