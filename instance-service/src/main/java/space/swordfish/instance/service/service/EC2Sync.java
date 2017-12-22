package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;

public interface EC2Sync {

	void syncAll();

	void syncByInstanceId(String instanceId);

	void saveStateChange(InstanceStateChange stateChange);

	WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler();

	WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(
			String instanceId);
}
