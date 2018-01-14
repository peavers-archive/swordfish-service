package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EC2CreateImpl extends EC2BaseService implements EC2Create {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public void process(Instance instance) {
        String keyName = ec2KeyPair.setName(instance);

        instance.setId(createUniqueId(keyName));
        instance.setUserId(authenticationService.getCurrentUser().getId());
        instance.setKeyName(keyName);
        instance.setKeyBlob(ec2KeyPair.create(instance));

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withInstanceType(instance.getInstanceType())
                .withImageId(instance.getImageId())
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds(instance.getSecurityGroupId())
                .withKeyName(keyName)
                .withSubnetId(instance.getSubnetId())
                .withTagSpecifications(buildTags(instance));

        instanceRepository.save(instance);
        refreshClientInstance(instance);

        ec2UserClient.amazonEC2Async().runInstancesAsync(runInstancesRequest,
                new AsyncHandler<RunInstancesRequest, RunInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong creating the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(RunInstancesRequest request, RunInstancesResult result) {
                        onSuccessCreate(result, instance);
                    }
                });
    }

    private TagSpecification buildTags(Instance instance) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Name", instance.getName()));
        tags.add(new Tag("Production", String.valueOf(instance.isProduction())));
        tags.add(new Tag("UserId", authenticationService.getCurrentUser().getId()));
        tags.add(new Tag("Swordfish", "true"));

        TagSpecification tagSpecification = new TagSpecification();
        tagSpecification.setTags(tags);
        tagSpecification.setResourceType(ResourceType.Instance);

        return tagSpecification;
    }

    private void onSuccessCreate(RunInstancesResult result, Instance instance) {
        com.amazonaws.services.ec2.model.Instance newInstance = result.getReservation().getInstances().get(0);
        instance.setInstanceId(newInstance.getInstanceId());

        instanceRepository.save(instance);
        refreshClientInstance(instance);
        ec2Sync.syncByInstance(instance);

        ec2UserClient.amazonEC2Async().waiters()
                .instanceRunning()
                .runAsync(ec2Waiter.describeInstancesRequestWaiterParameters(instance.getInstanceId()), ec2Waiter.describeInstancesRequestWaiterHandler());
    }
}