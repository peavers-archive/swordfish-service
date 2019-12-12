/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

@Slf4j
@Service
public class EC2RebootImpl extends EC2BaseService implements EC2Reboot {

  @Autowired private EC2UserClient ec2UserClient;

  @Override
  public void process(Instance instance) {
    ec2UserClient
        .amazonEC2Async()
        .rebootInstancesAsync(
            new RebootInstancesRequest().withInstanceIds(instance.getInstanceId()),
            new AsyncHandler<RebootInstancesRequest, RebootInstancesResult>() {
              @Override
              public void onError(Exception exception) {
                log.warn(
                    "something went wrong starting the server {}", exception.getLocalizedMessage());
              }

              @Override
              public void onSuccess(RebootInstancesRequest request, RebootInstancesResult result) {
                ec2UserClient
                    .amazonEC2Async()
                    .waiters()
                    .instanceRunning()
                    .runAsync(
                        ec2Waiter.describeInstancesRequestWaiterParameters(
                            instance.getInstanceId()),
                        ec2Waiter.describeInstancesRequestWaiterHandler());
              }
            });
  }
}
