package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EC2SyncImpl extends EC2BaseService implements EC2Sync {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public Iterable<Instance> getAll() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async().describeInstances(request);

        List<Instance> returnedInstances = new ArrayList<>();

        for (Reservation reservation : response.getReservations()) {
            for (com.amazonaws.services.ec2.model.Instance instance : reservation.getInstances()) {

                Instance instanceDetails = getInstanceDetails(instance);

                if (instanceDetails != null) {
                    returnedInstances.add(instanceDetails);
                }

            }
        }

        return returnedInstances;
    }

    @Override
    public Instance getByInstance(Instance instance) {
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId());
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async(instance.getUserId()).describeInstances(request);

        return getInstanceDetails(response.getReservations().get(0).getInstances().get(0));
    }

    private Instance getInstanceDetails(com.amazonaws.services.ec2.model.Instance awsInstance) {

        // Don't show dead servers
        if (awsInstance.getState().getName().equals("terminated")) {
            return null;
        }

        // Don't show servers that don't belong to swordfish
        if (!awsInstance.getTags().contains(new Tag().withKey("Swordfish").withValue("true"))) {
            return null;
        }

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

        return instance;
    }
}
