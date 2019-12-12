/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

@Slf4j
@Service
public class EC2StopImpl extends EC2BaseService implements EC2Stop {

  @Autowired private EC2UserClient ec2UserClient;

  @Override
  public void process(AmazonEC2Async amazonEC2Async, Instance instance) {
    amazonEC2Async.stopInstancesAsync(
        new StopInstancesRequest().withInstanceIds(instance.getInstanceId()),
        new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
          @Override
          public void onError(Exception exception) {
            log.warn(
                "something went wrong stopping the server {}", exception.getLocalizedMessage());
          }

          @Override
          public void onSuccess(StopInstancesRequest request, StopInstancesResult result) {
            onSuccessStop(instance);
          }
        });
  }

  @Override
  public void process(Instance instance) {
    ec2UserClient
        .amazonEC2Async()
        .stopInstancesAsync(
            new StopInstancesRequest().withInstanceIds(instance.getInstanceId()),
            new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
              @Override
              public void onError(Exception exception) {
                log.warn(
                    "something went wrong stopping the server {}", exception.getLocalizedMessage());
              }

              @Override
              public void onSuccess(StopInstancesRequest request, StopInstancesResult result) {
                onSuccessStop(instance);
              }
            });
  }

  private void onSuccessStop(Instance instance) {
    instanceRepository.save(instance);
    refreshClient(instance);
    ec2Sync.syncByInstance(instance);

    ec2UserClient
        .amazonEC2Async()
        .waiters()
        .instanceStopped()
        .runAsync(
            ec2Waiter.describeInstancesRequestWaiterParameters(instance.getInstanceId()),
            ec2Waiter.describeInstancesRequestWaiterHandler());
  }
}
