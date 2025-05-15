package no.ntnu.gr10.bachelor_gateway.security.grpc;

import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable gRPC server reflection in the gateway.
 * <p>
 * Registers the {@link io.grpc.protobuf.services.ProtoReflectionService} so that
 * clients and tools can discover available gRPC services and their methods at runtime.
 * </p>
 *
 * @author Daniel Neset
 * @version 03.05.2025
 */
@Configuration
public class GrpcReflectionConfig {
  @Bean
  public BindableService reflectionService() {
    return ProtoReflectionService.newInstance();
  }
}
