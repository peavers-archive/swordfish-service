/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;

public interface EC2Waiter {

  WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(
      String instanceId);

  WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler();
}
