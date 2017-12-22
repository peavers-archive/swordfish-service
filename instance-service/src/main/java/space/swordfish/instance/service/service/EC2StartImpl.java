package space.swordfish.instance.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EC2StartImpl implements EC2Start {

	private final AmazonEC2Async amazonEC2Async;
	private final EC2Sync ec2Sync;

	@Autowired
	public EC2StartImpl(AmazonEC2Async amazonEC2Async, EC2Sync ec2Sync) {
		this.amazonEC2Async = amazonEC2Async;
		this.ec2Sync = ec2Sync;
	}

	@Override
	public void start(String instanceId) {
		amazonEC2Async.startInstancesAsync(
				new StartInstancesRequest().withInstanceIds(instanceId),
				new AsyncHandler<StartInstancesRequest, StartInstancesResult>() {
					@Override
					public void onError(Exception exception) {
						log.warn("something went wrong starting the server {}",
								exception.getLocalizedMessage());
					}

					@Override
					public void onSuccess(StartInstancesRequest request,
							StartInstancesResult result) {
						List<InstanceStateChange> instanceStateChanges = result
								.getStartingInstances();

						for (InstanceStateChange stateChange : instanceStateChanges) {

							ec2Sync.saveStateChange(stateChange);

							amazonEC2Async.waiters().instanceRunning().runAsync(
									ec2Sync.describeInstancesRequestWaiterParameters(
											stateChange.getInstanceId()),
									ec2Sync.describeInstancesRequestWaiterHandler());
						}
					}
				});
	}

}
