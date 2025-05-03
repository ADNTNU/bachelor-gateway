package no.ntnu.gr10.bachelor_gateway.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class JwtAuthInterceptor implements ServerInterceptor {
  @Override
  public <ReqT, ResT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, ResT> call,
          Metadata headers,
          ServerCallHandler<ReqT, ResT> next) {

    // store the incoming headers in Context so downstream services can grab them
    Context ctx = Context.current()
            .withValue(SecurityContext.CURRENT_METADATA, headers);

    return Contexts.interceptCall(ctx, call, headers, next);
  }
}
