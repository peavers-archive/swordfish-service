package space.swordfish.instance.service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.*;

import lombok.extern.slf4j.Slf4j;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.instance.service.domain.Instance;

@Slf4j
@Service
public class EC2CreateImpl implements EC2Create {

	private final AmazonEC2Async amazonEC2Async;
	private final Auth0Service auth0Service;
	private final EC2Sync ec2Sync;
	@Value("${aws.defaults.securityGroup}")
	private String defaultSecurityGroupIds;

	@Autowired
	public EC2CreateImpl(AmazonEC2Async amazonEC2Async, Auth0Service auth0Service,
			EC2Sync ec2Sync) {

		this.amazonEC2Async = amazonEC2Async;
		this.auth0Service = auth0Service;
		this.ec2Sync = ec2Sync;
	}

	@Override
	public void create(Instance instance) {
		String userId = auth0Service.getUserId(instance.getUserToken());
		instance.setUserId(userId);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("Name", instance.getName()));
		tags.add(new Tag("Description", instance.getDescription()));
		tags.add(new Tag("Production", String.valueOf(instance.isProduction())));
		tags.add(new Tag("Static", String.valueOf(instance.isStaticIp())));
		tags.add(new Tag("UserId", userId));

		// All servers created with Swordfish are tagged as such
		tags.add(new Tag("Swordfish", "true"));

		TagSpecification tagSpecification = new TagSpecification();
		tagSpecification.setTags(tags);
		tagSpecification.setResourceType(ResourceType.Instance);

		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
				.withInstanceType(instance.getInstanceType())
				.withImageId(instance.getImageId()).withMinCount(1).withMaxCount(1)
				.withSecurityGroupIds(defaultSecurityGroupIds)
				.withKeyName(instance.getKeyName()).withSubnetId(instance.getSubnetId())
				.withTagSpecifications(tagSpecification);

		amazonEC2Async.runInstancesAsync(runInstancesRequest,
				new AsyncHandler<RunInstancesRequest, RunInstancesResult>() {
					@Override
					public void onError(Exception exception) {
						log.warn("something went wrong creating the server {}",
								exception.getLocalizedMessage());
					}

					@Override
					public void onSuccess(RunInstancesRequest request,
							RunInstancesResult result) {
						List<com.amazonaws.services.ec2.model.Instance> instances = result
								.getReservation().getInstances();

						for (com.amazonaws.services.ec2.model.Instance instance : instances) {

							ec2Sync.syncByInstanceId(instance.getInstanceId());
							log.info("Created new instance {}", instance);
							amazonEC2Async.waiters().instanceRunning().runAsync(
									ec2Sync.describeInstancesRequestWaiterParameters(
											instance.getInstanceId()),
									ec2Sync.describeInstancesRequestWaiterHandler());
						}
					}
				});
	}
}
