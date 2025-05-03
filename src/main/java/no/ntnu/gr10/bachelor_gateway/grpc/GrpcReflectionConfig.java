package no.ntnu.gr10.bachelor_gateway.grpc;

import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcReflectionConfig {
  @Bean
  public BindableService reflectionService() {
    return ProtoReflectionService.newInstance();
  }
}
