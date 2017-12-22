package space.swordfish.instance.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EC2RebootImpl implements EC2Reboot {

	private final AmazonEC2Async amazonEC2Async;

	@Autowired
	public EC2RebootImpl(AmazonEC2Async amazonEC2Async) {

		this.amazonEC2Async = amazonEC2Async;
	}

	@Override
	public void reboot(String instanceId) {
		amazonEC2Async.rebootInstancesAsync(
				new RebootInstancesRequest().withInstanceIds(instanceId),
				new AsyncHandler<RebootInstancesRequest, RebootInstancesResult>() {
					@Override
					public void onError(Exception exception) {
						log.warn("something went wrong starting the server {}",
								exception.getLocalizedMessage());
					}

					@Override
					public void onSuccess(RebootInstancesRequest request,
							RebootInstancesResult result) {
						log.info("rebooted {}", result);
					}
				});
	}
}
