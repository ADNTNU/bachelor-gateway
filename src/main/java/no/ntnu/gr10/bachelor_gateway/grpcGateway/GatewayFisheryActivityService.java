package no.ntnu.gr10.bachelor_gateway.grpcGateway;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.security.Scopes;
import no.ntnu.gr10.bachelor_gateway.security.grpc.ScopesAllowed;
import no.ntnu.gr10.bachelor_gateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.FisheryActivityServiceGrpc;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.GetFisheryActivityRequest;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ListFisheryActivitiesRequest;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ListFisheryActivitiesResponse;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ResponseFisheryActivity;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

/**
 * gRPC gateway service for proxying FisheryActivity operations to the upstream API.
 * <p>
 * This service enforces scope-based authorization via {@link ScopesAllowed} and
 * forwards incoming RPC calls to the configured remote stub, propagating metadata
 * (such as JWT headers) from the gateway context.
 * </p>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 */
@GrpcService
public class GatewayFisheryActivityService extends FisheryActivityServiceGrpc.FisheryActivityServiceImplBase {

  /**
   * Injected gRPC stub to communicate with the backend FisheryActivity API.
   */
  @GrpcClient("grpc-api")
  private FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub stub;


  /**
   * Retrieves a list of fishery activities for the authenticated user.
   * <p>
   * Requires the {@link Scopes#FISHERY_ACTIVITY} scope. The gateway will
   * attach the existing authentication metadata to the outbound call.
   * </p>
   *
   * @param request The RPC request containing optional filtering parameters
   * @param responseObserver The observer to receive the {@link ListFisheryActivitiesResponse}
   */
  @Override
  @ScopesAllowed(Scopes.FISHERY_ACTIVITY)
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
   * <p>
   * Requires the {@link Scopes#FISHERY_ACTIVITY} scope. Metadata propagation
   * ensures the backend can perform its own authorization checks.
   * </p>
   *
   * @param request The RPC request specifying the activity ID
   * @param responseObserver The observer to receive the {@link ResponseFisheryActivity}
   */
  @Override
  @ScopesAllowed(Scopes.FISHERY_ACTIVITY)
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
