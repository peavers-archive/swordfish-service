package space.swordfish.instance.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.instance.service.repository.InstanceRepository;

@Slf4j
@Service
public class EC2TerminateImpl implements EC2Terminate {

	private final InstanceRepository instanceRepository;
	private final AmazonEC2Async amazonEC2Async;

	@Autowired
	public EC2TerminateImpl(InstanceRepository instanceRepository,
			AmazonEC2Async amazonEC2Async) {
		this.instanceRepository = instanceRepository;
		this.amazonEC2Async = amazonEC2Async;
	}

	@Override
	public void terminate(String instanceId) {
		amazonEC2Async.terminateInstancesAsync(
				new TerminateInstancesRequest().withInstanceIds(instanceId),
				new AsyncHandler<TerminateInstancesRequest, TerminateInstancesResult>() {
					@Override
					public void onError(Exception exception) {
						log.warn("something went wrong terminating the server {}",
								exception.getLocalizedMessage());
					}

					@Override
					public void onSuccess(TerminateInstancesRequest request,
							TerminateInstancesResult result) {
						log.info("successfully terminated instance ", instanceId);

					}
				});

		instanceRepository.deleteByInstanceId(instanceId);
	}
}
