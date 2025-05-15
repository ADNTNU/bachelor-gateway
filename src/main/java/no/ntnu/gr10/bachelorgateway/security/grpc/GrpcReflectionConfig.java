package no.ntnu.gr10.bachelorgateway.security.grpc;

import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable gRPC server reflection in the gateway.
 *
 * <p>Registers the {@link io.grpc.protobuf.services.ProtoReflectionService} so that
 * clients and tools can discover available gRPC services and their methods at runtime.
 * </p>
 *
 * @author Daniel Neset
 * @version 03.05.2025
 */
@Configuration
public class GrpcReflectionConfig {
  /**
   * Registers the gRPC reflection service.
   *
   * <p>This bean is used to enable gRPC server reflection, allowing clients to discover
   * available services and methods at runtime.</p>
   *
   * @return a {@link BindableService} instance for gRPC reflection
   */
  @Bean
  public BindableService reflectionService() {
    return ProtoReflectionService.newInstance();
  }
}
