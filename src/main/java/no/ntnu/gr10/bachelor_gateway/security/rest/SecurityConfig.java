package no.ntnu.gr10.bachelor_gateway.security.rest;

import no.ntnu.gr10.bachelor_gateway.security.Scopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
   * Defines the security filter chain.
   *
   * @param httpSecurity the HttpSecurity to configure
   * @return the SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/auth/**").permitAll()
                    .pathMatchers("/ws-auth-token").permitAll()
                    .pathMatchers("/ws/data/**").permitAll()
                    .pathMatchers("/rest/**").hasAuthority(Scopes.FISHERY_ACTIVITY.getAuthority())
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
