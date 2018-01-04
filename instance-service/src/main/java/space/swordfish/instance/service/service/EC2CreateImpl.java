package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EC2CreateImpl implements EC2Create {

    @Value("${aws.defaults.securityGroup}")
    private String defaultSecurityGroupIds;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private Auth0Service auth0Service;

    @Autowired
    private EC2KeyPair keyPair;

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public void create(Instance instance) {
        String userToken = instance.getUserToken();
        String keyName = keyPair.setName(instance);

        // Custom data
        instance.setKeyName(keyName);
        instance.setKeyBlob(keyPair.create(instance));
        instance.setUserId(auth0Service.getUserIdFromToken(userToken));

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withInstanceType(instance.getInstanceType())
                .withImageId(instance.getImageId())
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds(defaultSecurityGroupIds)
                .withKeyName(keyName)
                .withSubnetId(instance.getSubnetId())
                .withTagSpecifications(buildTags(instance));

        instanceRepository.save(instance);

        ec2UserClient.amazonEC2Async(userToken).runInstancesAsync(runInstancesRequest,
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

    private String createUniqueId(com.amazonaws.services.ec2.model.Instance instance) {
        return UUID.nameUUIDFromBytes(instance.getInstanceId().getBytes()).toString();
    }
}