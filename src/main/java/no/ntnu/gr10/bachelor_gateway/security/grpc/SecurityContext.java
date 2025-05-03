package no.ntnu.gr10.bachelor_gateway.security.grpc;

import io.grpc.Context;
import io.grpc.Metadata;

public class SecurityContext {

  private SecurityContext () {}

  public static final Metadata.Key<String> AUTH_HEADER =
          Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  public static final Context.Key<Metadata> CURRENT_METADATA =
          Context.key("current-metadata");

  public static final Context.Key<Long> COMPANY_ID_CTX_KEY =
          Context.key("companyId");

  public static final Context.Key<java.util.List<String>> AUTHORITIES_CTX_KEY =
          Context.key("authorities");
}
