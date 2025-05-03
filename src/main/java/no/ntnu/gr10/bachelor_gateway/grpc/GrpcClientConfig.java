package no.ntnu.gr10.bachelor_gateway.grpc;

import io.grpc.ManagedChannel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.FisheryActivityServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
  /**
   * This name (“fishery-service”) must match the one in your
   * application.yml under grpc.client.fishery-service.address
   */
  @GrpcClient("fishery-service")
  private FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub fisheryStub;

  // Expose it as a bean if you like; or else inject @GrpcClient directly into your Gateway service.
  public FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub getFisheryStub() {
    return fisheryStub;
  }
}
