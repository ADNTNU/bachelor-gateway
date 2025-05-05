package no.ntnu.gr10.bachelor_gateway.grcpApi;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.security.grpc.GrpcClientConfig;
import no.ntnu.gr10.bachelor_gateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.*;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

@GrpcService
public class GatewayFisheryService extends FisheryActivityServiceGrpc.FisheryActivityServiceImplBase {

  private final FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub stub;

  public GatewayFisheryService(GrpcClientConfig cfg) {
    this.stub = cfg.getFisheryStub();
  }

  @Override
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

  @Override
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
