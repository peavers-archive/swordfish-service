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
        String userToken = instance.getUserToken();
        String keyName = ec2KeyPair.setName(instance);

        // Every key name must be unique, so we use this to build the seed of the ID
        instance.setId(createUniqueId(keyName));

        // We need the userId set here as just about everything else uses it
        instance.setUserId(auth0Service.getUserIdFromToken(userToken));

        instance.setKeyName(keyName);
        instance.setKeyBlob(ec2KeyPair.create(instance));

        instanceRepository.save(instance);

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withInstanceType(instance.getInstanceType())
                .withImageId(instance.getImageId())
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds(instance.getSecurityGroupId())
                .withKeyName(keyName)
                .withSubnetId(instance.getSubnetId())
                .withTagSpecifications(buildTags(instance));

        ec2UserClient.amazonEC2Async(instance.getUserId()).runInstancesAsync(runInstancesRequest,
                new AsyncHandler<RunInstancesRequest, RunInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong creating the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(RunInstancesRequest request, RunInstancesResult result) {

                    }
                });
    }

    private TagSpecification buildTags(Instance instance) {
        String userId = auth0Service.getUserIdFromToken(instance.getUserToken());

        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Name", instance.getName()));
        tags.add(new Tag("Production", String.valueOf(instance.isProduction())));
        tags.add(new Tag("UserId", userId));
        tags.add(new Tag("Swordfish", "true"));

        TagSpecification tagSpecification = new TagSpecification();
        tagSpecification.setTags(tags);
        tagSpecification.setResourceType(ResourceType.Instance);

        return tagSpecification;
    }
}