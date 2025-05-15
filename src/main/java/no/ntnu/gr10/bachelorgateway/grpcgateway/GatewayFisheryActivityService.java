package no.ntnu.gr10.bachelorgateway.grpcgateway;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelorgateway.security.Scope;
import no.ntnu.gr10.bachelorgateway.security.grpc.ScopesAllowed;
import no.ntnu.gr10.bachelorgateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelorgrpcapi.fisheryactivity.FisheryActivityServiceGrpc;
import no.ntnu.gr10.bachelorgrpcapi.fisheryactivity.GetFisheryActivityRequest;
import no.ntnu.gr10.bachelorgrpcapi.fisheryactivity.ListFisheryActivitiesRequest;
import no.ntnu.gr10.bachelorgrpcapi.fisheryactivity.ListFisheryActivitiesResponse;
import no.ntnu.gr10.bachelorgrpcapi.fisheryactivity.ResponseFisheryActivity;

/**
 * gRPC gateway service for proxying FisheryActivity operations to the upstream API.
 *
 * <p>This service enforces scope-based authorization via {@link ScopesAllowed} and
 * forwards incoming RPC calls to the configured remote stub, propagating metadata
 * (such as JWT headers) from the gateway context.
 * </p>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 */
@GrpcService
public class GatewayFisheryActivityService
        extends FisheryActivityServiceGrpc.FisheryActivityServiceImplBase {

  /**
   * Injected gRPC stub to communicate with the backend FisheryActivity API.
   */
  @GrpcClient("grpc-api")
  private FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub stub;


  /**
   * Retrieves a list of fishery activities for the authenticated user.
   *
   * <p>Requires the {@link Scope#FISHERY_ACTIVITY} scope. The gateway will
   * attach the existing authentication metadata to the outbound call.
   * </p>
   *
   * @param request The RPC request containing optional filtering parameters
   * @param responseObserver The observer to receive the {@link ListFisheryActivitiesResponse}
   */
  @Override
  @ScopesAllowed(Scope.FISHERY_ACTIVITY)
  public void listFisheryActivities(
          ListFisheryActivitiesRequest request,
          StreamObserver<ListFisheryActivitiesResponse> responseObserver) {

    Metadata headers = SecurityContext.CURRENT_METADATA.get();

    var stubWithHeaders = stub.withInterceptors(
            newAttachHeadersInterceptor(headers)
    );

    try {
      var resp = stubWithHeaders.listFisheryActivities(request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (RuntimeException e) {
      responseObserver.onError(e);
    }
  }


  /**
   * Retrieves details for a specific fishery activity.
   *
   * <p>Requires the {@link Scope#FISHERY_ACTIVITY} scope. Metadata propagation
   * ensures the backend can perform its own authorization checks.
   * </p>
   *
   * @param request The RPC request specifying the activity ID
   * @param responseObserver The observer to receive the {@link ResponseFisheryActivity}
   */
  @Override
  @ScopesAllowed(Scope.FISHERY_ACTIVITY)
  public void getFisheryActivity(
          GetFisheryActivityRequest request,
          StreamObserver<ResponseFisheryActivity> responseObserver) {

    Metadata headers = SecurityContext.CURRENT_METADATA.get();
    var stubWithHeaders = stub.withInterceptors(
            newAttachHeadersInterceptor(headers)
    );

    try {
      var resp = stubWithHeaders.getFisheryActivity(request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (RuntimeException e) {
      responseObserver.onError(e);
    }
  }
}
