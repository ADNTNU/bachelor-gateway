package no.ntnu.gr10.bachelor_gateway.grpc;

import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.common.security.SecurityConstants;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.*;

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

    // 1) grab the Metadata we stored in JwtAuthInterceptor
    Metadata headers = SecurityContext.CURRENT_METADATA.get();

    // 2) bind it to the downstream stub
    var stubWithHeaders = MetadataUtils.attachHeaders(stub, headers);

    // 3) forward
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
    var stubWithHeaders = MetadataUtils.attachHeaders(stub, headers);

    try {
      var resp = stubWithHeaders.getFisheryActivity(request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (RuntimeException e) {
      responseObserver.onError(e);
    }
  }
}
