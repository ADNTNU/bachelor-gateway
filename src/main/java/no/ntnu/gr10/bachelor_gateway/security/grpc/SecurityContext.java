package no.ntnu.gr10.bachelor_gateway.security.grpc;

import io.grpc.Context;
import io.grpc.Metadata;


/**
 * Defines gRPC security-related context and metadata keys used within the gateway interceptor and services.
 *
 * @author Daniel Neset
 * @version 03.05.2025
 */
public class SecurityContext {

  private SecurityContext () {
    // Utility class; do not instantiate
  }


  /**
   * Metadata key for the HTTP Authorization header containing the Bearer token.
   */
  public static final Metadata.Key<String> AUTH_HEADER =
          Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);


  /**
   * Context key for storing and retrieving the current incoming metadata within gRPC calls.
   */
  public static final Context.Key<Metadata> CURRENT_METADATA =
          Context.key("current-metadata");


  /**
   * Context key for storing and retrieving the company ID extracted from the JWT.
   */
  public static final Context.Key<Long> COMPANY_ID_CTX_KEY =
          Context.key("companyId");


  /**
   * Context key for storing and retrieving the list of user authorities (scopes) extracted from the JWT.
   */
  public static final Context.Key<java.util.List<String>> AUTHORITIES_CTX_KEY =
          Context.key("authorities");
}
