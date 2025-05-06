package no.ntnu.gr10.bachelor_gateway.grpcGateway;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import no.ntnu.gr10.bachelor_gateway.security.grpc.SecurityContext;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.FisheryActivityServiceGrpc;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.GetFisheryActivityRequest;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ListFisheryActivitiesRequest;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ListFisheryActivitiesResponse;
import no.ntnu.gr10.bachelor_grpc_api.fisheryActivity.ResponseFisheryActivity;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

@GrpcService
public class GatewayFisheryActivityService extends FisheryActivityServiceGrpc.FisheryActivityServiceImplBase {

  @GrpcClient("grpc-api")
  private FisheryActivityServiceGrpc.FisheryActivityServiceBlockingStub stub;

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
