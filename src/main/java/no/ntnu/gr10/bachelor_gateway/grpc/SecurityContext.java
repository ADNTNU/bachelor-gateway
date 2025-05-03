package no.ntnu.gr10.bachelor_gateway.grpc;

import io.grpc.Context;
import io.grpc.Metadata;

public class SecurityContext {
  public static final Context.Key<Metadata> CURRENT_METADATA =
          Context.key("current-metadata");
}
