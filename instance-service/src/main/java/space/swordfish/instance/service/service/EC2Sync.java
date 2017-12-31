package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import space.swordfish.instance.service.domain.Instance;

public interface EC2Sync {

    Instance sync(Instance instance);

    Instance syncStateChange(InstanceStateChange stateChange);

    WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler();

    WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(
            Instance instanceId);
}
