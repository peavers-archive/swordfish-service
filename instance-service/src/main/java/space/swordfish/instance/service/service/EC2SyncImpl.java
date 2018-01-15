package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.common.auth0.services.Auth0Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EC2SyncImpl extends EC2BaseService implements EC2Sync {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private Auth0Service auth0Service;

    @Override
    public Iterable<Instance> syncAll(AmazonEC2Async amazonEC2Async) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = amazonEC2Async.describeInstances(request);

        return processResponse(response);
    }

    @Override
    public Iterable<Instance> syncAll() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async().describeInstances(request);

        return processResponse(response);
    }

    @Override
    public Instance syncByInstance(Instance instance) {
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId());
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async().describeInstances(request);

        return processResponse(response).get(0);
    }

    @Override
    public Instance syncByInstanceId(String id) {
        Instance instance = instanceRepository.findByInstanceId(id);

        return syncByInstance(instance);
    }

    @Override
    public List<String> instancesNotOnAmazon() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async().describeInstances(request);

        Iterable<Instance> localInstances = instanceRepository.findAll();
        List<com.amazonaws.services.ec2.model.Instance> remoteInstances = response.getReservations().get(0).getInstances();

        List<String> localInstanceIds = new ArrayList<>();
        List<String> remoteInstanceIds = new ArrayList<>();

        for (Instance instance : localInstances) {
            localInstanceIds.add(instance.getInstanceId());
        }

        for (com.amazonaws.services.ec2.model.Instance instance : remoteInstances) {
            remoteInstanceIds.add(instance.getInstanceId());
        }

        return (List<String>) ListUtils.removeAll(localInstanceIds, remoteInstanceIds);
    }

    private List<Instance> processResponse(DescribeInstancesResult response) {
        List<Instance> returnedInstances = new ArrayList<>();

        for (Reservation reservation : response.getReservations()) {
            for (com.amazonaws.services.ec2.model.Instance instance : reservation.getInstances()) {

                // Don't show dead servers or servers not tagged as belonging to Swordfish
                if (instance.getState().getName().equals("terminated") || !instance.getTags().contains(new Tag().withKey("Swordfish").withValue("true"))) {
                    continue;
                }

                Instance instanceDetails = getInstanceDetails(instance);
                returnedInstances.add(instanceDetails);
                instanceRepository.save(instanceDetails);
                refreshClient(instanceDetails);
            }
        }

        return returnedInstances;
    }

    private Instance getInstanceDetails(com.amazonaws.services.ec2.model.Instance awsInstance) {
        // If we've got extra data such as keys or names in the database grab em, otherwise create a new instance
        Instance instance = instanceRepository.findByKeyName(awsInstance.getKeyName());
        if (instance == null) {
            instance = new Instance();
        }

        // Grab the tags and load em into the instance object
        for (Tag tag : awsInstance.getTags()) {
            if (tag.getKey().equals("Name")) {
                instance.setName(tag.getValue());
            }
            if (tag.getKey().equals("UserId")) {
                instance.setUserId(tag.getValue());
            }
            if (tag.getKey().equals("Production")) {
                instance.setProduction(Boolean.parseBoolean(tag.getValue()));
            }
        }

        // Fetch the latest version of user data
        if (instance.getUserId() != null) {
            instance.setUserPicture(auth0Service.getUserProfilePicture(instance.getUserId()));
            instance.setUserName(auth0Service.getUserName(instance.getUserId()));
        }

        // Finish up with all the other stuff from AWS
        instance.setId(createUniqueId(awsInstance.getKeyName()));
        instance.setTags(awsInstance.getTags());
        instance.setInstanceType(awsInstance.getInstanceType());
        instance.setImageId(awsInstance.getImageId());
        instance.setSecurityGroupIds(awsInstance.getSecurityGroups());
        instance.setKeyName(awsInstance.getKeyName());
        instance.setSubnetId(awsInstance.getSubnetId());
        instance.setInstanceId(awsInstance.getInstanceId());
        instance.setState(awsInstance.getState().getName());
        instance.setPublicIp(awsInstance.getPublicIpAddress());
        instance.setPrivateIp(awsInstance.getPrivateIpAddress());
        instance.setCreated(awsInstance.getLaunchTime());

        instanceRepository.save(instance);

        return instance;
    }
}
