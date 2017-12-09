package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class InstanceEC2ServiceImpl implements InstanceEC2Service {

    private final InstanceRepository instanceRepository;
    private final AmazonEC2Async amazonEC2Async;
    private final JsonTransformService jsonTransformService;
    private final Auth0Service auth0Service;
    private final NotificationService notificationService;

    @Autowired
    public InstanceEC2ServiceImpl(InstanceRepository instanceRepository, AmazonEC2Async amazonEC2Async, JsonTransformService jsonTransformService, Auth0Service auth0Service, NotificationService notificationService) {
        this.instanceRepository = instanceRepository;
        this.amazonEC2Async = amazonEC2Async;
        this.jsonTransformService = jsonTransformService;
        this.auth0Service = auth0Service;
        this.notificationService = notificationService;
    }


    /**
     * Turn a instance on
     *
     * @param instanceId String
     */
    @Override
    public void start(String instanceId) {
        amazonEC2Async.startInstancesAsync(new StartInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<StartInstancesRequest, StartInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong starting the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(StartInstancesRequest request, StartInstancesResult result) {
                        List<InstanceStateChange> instanceStateChanges = result.getStartingInstances();

                        for (InstanceStateChange stateChange : instanceStateChanges) {

                            saveStateChange(stateChange);

                            amazonEC2Async.waiters()
                                    .instanceRunning()
                                    .runAsync(describeInstancesRequestWaiterParameters(stateChange.getInstanceId()), describeInstancesRequestWaiterHandler());
                        }
                    }
                });
    }


    /**
     * Turn a instance off
     *
     * @param instanceId String
     */
    @Override
    public void stop(String instanceId) {
        amazonEC2Async.stopInstancesAsync(new StopInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong stopping the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(StopInstancesRequest request, StopInstancesResult result) {
                        List<InstanceStateChange> instanceStateChanges = result.getStoppingInstances();

                        for (InstanceStateChange stateChange : instanceStateChanges) {

                            saveStateChange(stateChange);

                            amazonEC2Async.waiters()
                                    .instanceStopped()
                                    .runAsync(describeInstancesRequestWaiterParameters(stateChange.getInstanceId()), describeInstancesRequestWaiterHandler());
                        }
                    }
                });
    }

    @Override
    public void reboot(String instanceId) {
        amazonEC2Async.rebootInstancesAsync(new RebootInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<RebootInstancesRequest, RebootInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong starting the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(RebootInstancesRequest request, RebootInstancesResult result) {
                        log.info("rebooted {}", result);
                    }
                });
    }

    /**
     * Terminate a instance. Warning this is forever and can't be undone.
     *
     * @param instanceId String
     */
    @Override
    public void terminate(String instanceId) {
        amazonEC2Async.terminateInstancesAsync(new TerminateInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<TerminateInstancesRequest, TerminateInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong terminating the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(TerminateInstancesRequest request, TerminateInstancesResult result) {
                        log.info("successfully terminated instance ", instanceId);

                    }
                });

        instanceRepository.deleteByInstanceId(instanceId);
    }

    /**
     * Create a new instance on AWS
     *
     * @param instance
     */
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

        TagSpecification tagSpecification = new TagSpecification();
        tagSpecification.setTags(tags);
        tagSpecification.setResourceType(ResourceType.Instance);

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withInstanceType(instance.getInstanceType())
                .withImageId(instance.getImageId())
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds(instance.getDefaultSecurityGroupIds())
                .withKeyName(instance.getKeyName())
                .withSubnetId(instance.getSubnetId())
                .withTagSpecifications(tagSpecification);

        amazonEC2Async.runInstancesAsync(runInstancesRequest,
                new AsyncHandler<RunInstancesRequest, RunInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong creating the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(RunInstancesRequest request, RunInstancesResult result) {
                        List<com.amazonaws.services.ec2.model.Instance> instances = result.getReservation().getInstances();

                        for (com.amazonaws.services.ec2.model.Instance instance : instances) {

                            syncByInstanceId(instance.getInstanceId());
                            log.info("Created new instance {}", instance);
                            amazonEC2Async.waiters()
                                    .instanceRunning()
                                    .runAsync(describeInstancesRequestWaiterParameters(instance.getInstanceId()), describeInstancesRequestWaiterHandler());
                        }
                    }
                });
    }

    /**
     * Fetch all data from AWS and either syncByInstanceId it as a new instance locally, or update a local instance with new data.
     */
    @Override
    public void syncAll() {
        amazonEC2Async.describeInstancesAsync(new DescribeInstancesRequest(),
                new AsyncHandler<DescribeInstancesRequest, DescribeInstancesResult>() {

                    /**
                     * Writes the instance to storage, send notification to frontend client
                     * @param awsInstance Instance data from Amazon
                     */
                    private void save(com.amazonaws.services.ec2.model.Instance awsInstance) {
                        Instance instance = instanceBuilder(awsInstance);

                        if (!instance.getState().equals("terminated")) {
                            instanceRepository.save(instance);

                            notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(instance));
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong syncing the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(DescribeInstancesRequest request, DescribeInstancesResult describeInstancesResult) {
                        describeInstancesResult.getReservations().forEach(reservation -> reservation.getInstances().forEach(this::save));

                        // Code below removes instances from the Database that are no longer on AWS.
                        List<com.amazonaws.services.ec2.model.Instance> awsInstances = new ArrayList<>();
                        describeInstancesResult.getReservations().stream().map(Reservation::getInstances).forEach(awsInstances::addAll);

                        Collection<String> localInstanceIds = new ArrayList<>();
                        for (Instance instance : instanceRepository.findAll()) {
                            localInstanceIds.add(instance.getInstanceId());
                        }

                        Collection<String> awsInstanceIds = new ArrayList<>();
                        for (com.amazonaws.services.ec2.model.Instance awsInstance : awsInstances) {
                            awsInstanceIds.add(awsInstance.getInstanceId());
                        }

                        List<String> sourceList = new ArrayList<>(awsInstanceIds);
                        List<String> destinationList = new ArrayList<>(localInstanceIds);

                        destinationList.removeAll(sourceList);

                        for (String instanceId : destinationList) {
                            instanceRepository.deleteByInstanceId(instanceId);
                        }
                    }
                });
    }

    /**
     * Handles the saving into the database, however before doing the actual syncByInstanceId operation, go out and double check
     * the data from AWS. This is useful for when things like public IPs take a few extra seconds to syncAll through.
     *
     * @param instanceId String
     */
    private void syncByInstanceId(String instanceId) {
        amazonEC2Async.describeInstancesAsync(new DescribeInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<DescribeInstancesRequest, DescribeInstancesResult>() {

                    private void save(com.amazonaws.services.ec2.model.Instance awsInstance) {
                        Instance instance = instanceBuilder(awsInstance);

                        instanceRepository.save(instance);

                        log.info("Syncing server {}", instance);

                        notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(instance));
                    }

                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong saving the server {}", exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(DescribeInstancesRequest request, DescribeInstancesResult describeInstancesResult) {
                        describeInstancesResult.getReservations().forEach(reservation -> reservation.getInstances().forEach(this::save));
                    }
                });
    }

    private Instance instanceBuilder(com.amazonaws.services.ec2.model.Instance awsInstance) {
        List<Tag> tags = awsInstance.getTags();

        String name = "unknown";
        String description = "unknown";
        String userId = "unknown";
        boolean production = false;
        boolean staticIp = false;

        if (!tags.isEmpty()) {
            for (Tag tag : tags) {
                if (tag.getKey().equals("Name")) {
                    name = tag.getValue();
                }

                if (tag.getKey().equals("Description")) {
                    description = tag.getValue();
                }

                if (tag.getKey().equals("Production")) {
                    production = Boolean.valueOf(tag.getValue());
                }

                if (tag.getKey().equals("Static")) {
                    staticIp = Boolean.valueOf(tag.getValue());
                }

                if (tag.getKey().equals("UserId")) {
                    userId = tag.getValue();
                }
            }
        }

        Instance instance = new Instance();

        instance.setId(UUID.nameUUIDFromBytes(awsInstance.getInstanceId().getBytes()).toString());
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
        instance.setName(name);
        instance.setDescription(description);
        instance.setProduction(production);
        instance.setStaticIp(staticIp);
        instance.setUserId(userId);
        instance.setUserName(auth0Service.getUserName(userId));
        instance.setUserPicture(auth0Service.getUserProfilePicture(userId));

        return instance;
    }

    private WaiterParameters<DescribeInstancesRequest> describeInstancesRequestWaiterParameters(String instanceId) {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);

        return new WaiterParameters<DescribeInstancesRequest>().withRequest(describeInstancesRequest);
    }

    private WaiterHandler<DescribeInstancesRequest> describeInstancesRequestWaiterHandler() {
        return new WaiterHandler<DescribeInstancesRequest>() {
            @Override
            public void onWaitSuccess(DescribeInstancesRequest request) {
                List<String> instanceIds = request.getInstanceIds();

                for (String instanceId : instanceIds) {
                    syncByInstanceId(instanceId);
                }
            }

            @Override
            public void onWaitFailure(Exception e) {
                log.warn("something went wrong waiting for AWS {}", e.getLocalizedMessage());
            }
        };
    }

    private void saveStateChange(InstanceStateChange stateChange) {
        Instance instance = instanceRepository.findByInstanceId(stateChange.getInstanceId());
        instance.setState(stateChange.getCurrentState().getName());

        instanceRepository.save(instance);

        notificationService.send("server_refresh", "server_refresh", jsonTransformService.write(instance));
    }
}
