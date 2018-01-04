package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EC2SyncImpl implements EC2Sync {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public Iterable<Instance> getAll() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2UserClient.amazonEC2Async().describeInstances(request);

        List<Instance> returnedInstances = new ArrayList<>();

        for (Reservation reservation : response.getReservations()) {
            for (com.amazonaws.services.ec2.model.Instance instance : reservation.getInstances()) {

                Instance saveAndReturn = saveAndReturn(instance);

                if (saveAndReturn != null) {
                    returnedInstances.add(saveAndReturn);
                }

            }
        }

        return returnedInstances;
    }

    private Instance saveAndReturn(com.amazonaws.services.ec2.model.Instance awsInstance) {

        // Don't show dead servers
        if (awsInstance.getState().getName().equals("terminated")) {
            return null;
        }

        // Don't show servers that don't belong to swordfish
        Tag tag = new Tag().withKey("Swordfish").withValue("true");
        if (!awsInstance.getTags().contains(tag)) {
            return null;
        }

        // If we've got extra data such as keys or names in the database grab em, otherwise create a new instance
        Instance instance = instanceRepository.findByKeyName(awsInstance.getKeyName());
        if (instance == null) {
            instance = new Instance();
        }

        instance.setId(createUniqueId(awsInstance));
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

    private String createUniqueId(com.amazonaws.services.ec2.model.Instance instance) {
        return UUID.nameUUIDFromBytes(instance.getInstanceId().getBytes()).toString();
    }
}
