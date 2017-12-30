package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.repository.InstanceRepository;

import java.util.List;

@Slf4j
@Service
public class EC2StopImpl implements EC2Stop {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AmazonEC2Async amazonEC2Async;

    @Autowired
    private EC2Sync ec2Sync;

    @Override
    public void stop(String instanceId) {
        amazonEC2Async.stopInstancesAsync(
                new StopInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<StopInstancesRequest, StopInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong stopping the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(StopInstancesRequest request,
                                          StopInstancesResult result) {
                        List<InstanceStateChange> instanceStateChanges = result
                                .getStoppingInstances();

                        for (InstanceStateChange stateChange : instanceStateChanges) {

                            ec2Sync.syncStateChange(stateChange);

                            amazonEC2Async.waiters().instanceStopped().runAsync(
                                    ec2Sync.describeInstancesRequestWaiterParameters(instanceRepository.findByInstanceId(stateChange.getInstanceId())),
                                    ec2Sync.describeInstancesRequestWaiterHandler());
                        }
                    }
                });
    }

}
