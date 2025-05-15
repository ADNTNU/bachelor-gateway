package no.ntnu.gr10.bachelorgateway.grpcgateway;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelorgateway.security.Scope;
import no.ntnu.gr10.bachelorgateway.security.grpc.ScopesAllowed;
import no.ntnu.gr10.bachelorgateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelorgrpcapi.fishingfacility.FishingFacilityServiceGrpc;
import no.ntnu.gr10.bachelorgrpcapi.fishingfacility.GetFishingFacilityRequest;
import no.ntnu.gr10.bachelorgrpcapi.fishingfacility.ListFishingFacilitiesRequest;
import no.ntnu.gr10.bachelorgrpcapi.fishingfacility.ListFishingFacilitiesResponse;
import no.ntnu.gr10.bachelorgrpcapi.fishingfacility.ResponseFishingFacility;

/**
 * gRPC gateway service for proxying FishingFacility operations to the upstream API.
 *
 * <p>Enforces scope-based authorization via {@link ScopesAllowed} annotations and
 * forwards incoming requests to the backend stub, propagating authentication metadata.
 * </p>
 *
 * @author Daniel Neset
 * @version 15.05.2025
 */
@GrpcService
public class GatewayFishingFacilityService
        extends FishingFacilityServiceGrpc.FishingFacilityServiceImplBase {

  /**
   * Injected gRPC stub to communicate with the backend FishingFacility API.
   */
  @GrpcClient("grpc-api")
  private FishingFacilityServiceGrpc.FishingFacilityServiceBlockingStub stub;


  /**
   * Lists available fishing facilities for the authenticated user.
   *
   * <p>Requires the {@link Scope#FISHING_FACILITY} scope. The gateway attaches
   * existing metadata (e.g., JWT headers) to the outgoing call.
   * </p>
   *
   * @param request The RPC request containing filtering or pagination parameters
   * @param responseObserver The observer to receive the {@link ListFishingFacilitiesResponse}
   */
  @Override
  @ScopesAllowed(Scope.FISHING_FACILITY)
  public void listFishingFacilities(
          ListFishingFacilitiesRequest request,
          StreamObserver<ListFishingFacilitiesResponse> responseObserver) {

    Metadata headers = SecurityContext.CURRENT_METADATA.get();

    var stubWithHeaders = stub.withInterceptors(
            newAttachHeadersInterceptor(headers)
    );

    try {
      var resp = stubWithHeaders.listFishingFacilities(request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (RuntimeException e) {
      responseObserver.onError(e);
    }
  }


  /**
   * Retrieves details of a specific fishing facility.
   *
   * <p>Requires the {@link Scope#FISHING_FACILITY} scope. Authentication metadata
   * is propagated to allow backend validation.
   * </p>
   *
   * @param request The RPC request specifying the facility ID
   * @param responseObserver The observer to receive the {@link ResponseFishingFacility}
   */
  @Override
  @ScopesAllowed(Scope.FISHING_FACILITY)
  public void getFishingFacility(
          GetFishingFacilityRequest request,
          StreamObserver<ResponseFishingFacility> responseObserver) {

    Metadata headers = SecurityContext.CURRENT_METADATA.get();
    var stubWithHeaders = stub.withInterceptors(
            newAttachHeadersInterceptor(headers)
    );

    try {
      var resp = stubWithHeaders.getFishingFacility(request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (RuntimeException e) {
      responseObserver.onError(e);
    }
  }

}
