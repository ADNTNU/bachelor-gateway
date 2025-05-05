package no.ntnu.gr10.bachelor_gateway.security.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.FisheryActivityServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

//  TODO: Remove old code when verified that the new code works
  /**
   * This name (“fishery-service”) must match the one in your
   * application.yml under grpc.client.fishery-service.address
   */
//  @GrpcClient("fishery-service")
//  private FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub fisheryStub;
//
//  // Expose it as a bean if you like; or else inject @GrpcClient directly into your Gateway service.
//  public FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub getFisheryStub() {
//    return fisheryStub;
//  }

  @Value("${grpc.client.fishery-service.address}")
  private String grpcServerAddress;

  @Bean
  public ManagedChannel fisheryChannel() {
    // Remove "static://" prefix and connect to the address
    String address = grpcServerAddress.replace("static://", "");

    String[] parts = address.split(":");
    String host = parts[0];
    int port = (parts.length > 1) ? Integer.parseInt(parts[1]) : 8080;

    return ManagedChannelBuilder.forAddress(host, port) // Ensure you specify port if not included
            .usePlaintext()  // Use plaintext as per your configuration
            .build();
  }

  @Bean
  public FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub getFisheryStub() {
    return FisheryActivityServiceGrpc.newBlockingStub(fisheryChannel());
  }
}
