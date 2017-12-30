package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.List;

@Slf4j
@Service
public class EC2StartImpl implements EC2Start {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AmazonEC2Async amazonEC2Async;

    @Autowired
    private EC2Sync ec2Sync;

    @Override
    public void start(String instanceId) {
        amazonEC2Async.startInstancesAsync(
                new StartInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<StartInstancesRequest, StartInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong starting the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(StartInstancesRequest request,
                                          StartInstancesResult result) {
                        List<InstanceStateChange> instanceStateChanges = result
                                .getStartingInstances();

                        for (InstanceStateChange stateChange : instanceStateChanges) {
                            ec2Sync.syncStateChange(stateChange);

                            amazonEC2Async.waiters().instanceRunning().runAsync(
                                    ec2Sync.describeInstancesRequestWaiterParameters(instanceRepository.findByInstanceId(stateChange.getInstanceId())),
                                    ec2Sync.describeInstancesRequestWaiterHandler());
                        }
                    }
                });
    }

}
