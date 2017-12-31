package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.List;

@Slf4j
@Service
public class EC2SyncImpl implements EC2Sync {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AmazonEC2Async amazonEC2Async;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private Auth0Service auth0Service;

    /**
     * Handles the saving into the database, however before doing the actual
     * sync operation, go out and double check the data from AWS. This is
     * useful for when things like public IPs take a few extra seconds to syncAll through.
     */
    @Override
    public Instance sync(Instance instance) {
        amazonEC2Async.describeInstancesAsync(
                new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId()),
                new AsyncHandler<DescribeInstancesRequest, DescribeInstancesResult>() {

                    private void save(com.amazonaws.services.ec2.model.Instance awsInstance) {
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
                        instance.setUserPicture(auth0Service.getUser(instance.getUserId()).getUserMetadata().containsKey("picture") ? (String) auth0Service.getUser(instance.getUserId()).getUserMetadata().get("picture") : auth0Service.getUserProfilePicture(instance.getUserId()));

                        instanceRepository.save(instance);

                        notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(instance));
                    }

                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong saving the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(DescribeInstancesRequest request,
                                          DescribeInstancesResult describeInstancesResult) {
                        describeInstancesResult.getReservations()
                                .forEach(reservation -> reservation.getInstances()
                                        .forEach(this::save));
                    }
                });

        return instance;
    }


    @Override
    public Instance syncStateChange(InstanceStateChange stateChange) {
        Instance instance = instanceRepository
                .findByInstanceId(stateChange.getInstanceId());
        instance.setState(stateChange.getCurrentState().getName());

        instanceRepository.save(instance);

        return instance;
    }

    @Override
    public WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(
            Instance instance) {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                .withInstanceIds(instance.getInstanceId());

        return new WaiterParameters<DescribeInstancesRequest>()
                .withRequest(describeInstancesRequest);
    }

    @Override
    public WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler() {

        return new WaiterHandler<DescribeInstancesRequest>() {
            @Override
            public void onWaitSuccess(DescribeInstancesRequest request) {
                List<String> instanceIds = request.getInstanceIds();

                for (String instanceId : instanceIds) {
                    sync(instanceRepository.findByInstanceId(instanceId));
                }
            }

            @Override
            public void onWaitFailure(Exception e) {
                log.warn("something went wrong waiting for AWS {}",
                        e.getLocalizedMessage());
            }
        };
    }
}
