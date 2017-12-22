package space.swordfish.instance.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EC2StopImpl implements EC2Stop {

	private final AmazonEC2Async amazonEC2Async;
	private final EC2Sync ec2Sync;

	@Autowired
	public EC2StopImpl(AmazonEC2Async amazonEC2Async, EC2Sync ec2Sync) {
		this.amazonEC2Async = amazonEC2Async;
		this.ec2Sync = ec2Sync;
	}

	@Override
	public void stop(String instanceId) {
		amazonEC2Async.stopInstancesAsync(
				new StopInstancesRequest().withInstanceIds(instanceId),
				new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
					@Override
					public void onError(Exception exception) {
						log.warn("something went wrong stopping the server {}",
								exception.getLocalizedMessage());
					}

					@Override
					public void onSuccess(StopInstancesRequest request,
							StopInstancesResult result) {
						List<InstanceStateChange> instanceStateChanges = result
								.getStoppingInstances();

						for (InstanceStateChange stateChange : instanceStateChanges) {

							ec2Sync.saveStateChange(stateChange);

							amazonEC2Async.waiters().instanceStopped().runAsync(
									ec2Sync.describeInstancesRequestWaiterParameters(
											stateChange.getInstanceId()),
									ec2Sync.describeInstancesRequestWaiterHandler());
						}
					}
				});
	}

}
