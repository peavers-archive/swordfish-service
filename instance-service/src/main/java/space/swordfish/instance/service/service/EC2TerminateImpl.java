package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;
import space.swordfish.instance.service.repository.InstanceRepository;

@Slf4j
@Service
public class EC2TerminateImpl implements EC2Terminate {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AmazonEC2Async amazonEC2Async;

    @Autowired
    private EC2KeyPair keyPair;

    @Override
    public void terminate(String instanceId) {
        Instance instance = instanceRepository.findByInstanceId(instanceId);

        if (instance.getKeyName().contains("swordfish-")) {
            keyPair.delete(instance);
        }

        amazonEC2Async.terminateInstancesAsync(
                new TerminateInstancesRequest().withInstanceIds(instanceId),
                new AsyncHandler<TerminateInstancesRequest, TerminateInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong terminating the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(TerminateInstancesRequest request,
                                          TerminateInstancesResult result) {
                        log.info("successfully terminated instance ", instanceId);

                    }
                });

        instanceRepository.deleteByInstanceId(instanceId);
    }

}
