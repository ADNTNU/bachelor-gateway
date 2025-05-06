package no.ntnu.gr10.bachelor_gateway.grpcGateway;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelor_grpc_api.fishingFacility.*;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

@GrpcService
public class GatewayFishingFacilityService extends FishingFacilityServiceGrpc.FishingFacilityServiceImplBase {

  @GrpcClient("grpc-api")
  private FishingFacilityServiceGrpc.FishingFacilityServiceBlockingStub stub;

  @Override
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

  @Override
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
