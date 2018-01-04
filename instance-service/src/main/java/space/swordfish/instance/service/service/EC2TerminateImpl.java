package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

@Slf4j
@Service
public class EC2TerminateImpl extends EC2BaseService implements EC2Terminate {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public void process(Instance instance) {
        if (instance.getKeyName().contains("swordfish-")) {
            ec2KeyPair.delete(instance);
        }

        ec2UserClient.amazonEC2Async(instance.getUserId()).terminateInstancesAsync(
                new TerminateInstancesRequest().withInstanceIds(instance.getInstanceId()),
                new AsyncHandler<TerminateInstancesRequest, TerminateInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong terminating the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(TerminateInstancesRequest request, TerminateInstancesResult result) {
                    }
                });

        instanceRepository.delete(instance);
    }

}
