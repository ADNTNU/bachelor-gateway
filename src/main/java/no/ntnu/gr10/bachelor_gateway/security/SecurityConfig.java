package no.ntnu.gr10.bachelor_gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up authentication and authorization.
 * This class configures the security filter chain, authentication manager, and password encoder.
 *
 * @author Anders Lund
 * @version 05.04.2025
 */
@Configuration
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Constructs a new SecurityConfig.
   *
   * @param userDetailsService the service to load user-specific data
   * @param jwtAuthenticationFilter the filter for JWT authentication
   */
  public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter){
    this.userDetailsService = userDetailsService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  /**
   * Configures authentication using the provided UserDetailsService.
   *
   * @param auth the AuthenticationManagerBuilder to configure
   * @throws Exception if an error occurs during configuration
   */
  @Autowired
  protected void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }


  /**
   * Defines the security filter chain.
   *
   * @param httpSecurity the HttpSecurity to configure
   * @return the SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  /**
   * Creates an AuthenticationManager bean.
   *
   * @param config the AuthenticationConfiguration
   * @return the AuthenticationManager
   * @throws Exception if an error occurs while retrieving the manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Returns a PasswordEncoder that performs no encoding.
   *
   * @return a PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

}
