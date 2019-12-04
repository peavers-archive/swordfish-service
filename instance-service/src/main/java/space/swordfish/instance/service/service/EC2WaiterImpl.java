/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EC2WaiterImpl extends EC2BaseService implements EC2Waiter {

  @Override
  public WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(
      String instanceId) {
    DescribeInstancesRequest describeInstancesRequest =
        new DescribeInstancesRequest().withInstanceIds(instanceId);

    return new WaiterParameters<DescribeInstancesRequest>().withRequest(describeInstancesRequest);
  }

  @Override
  public WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler() {
    return new WaiterHandler<DescribeInstancesRequest>() {
      @Override
      public void onWaitSuccess(DescribeInstancesRequest request) {
        List<String> instanceIds = request.getInstanceIds();

        for (String instanceId : instanceIds) {
          ec2Sync.syncByInstanceId(instanceId);
        }
      }

      @Override
      public void onWaitFailure(Exception e) {
        log.warn("something went wrong waiting for AWS {}", e.getLocalizedMessage());
      }
    };
  }
}
