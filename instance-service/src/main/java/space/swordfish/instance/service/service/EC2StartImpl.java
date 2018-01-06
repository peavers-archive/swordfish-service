package space.swordfish.instance.service.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

@Slf4j
@Service
public class EC2StartImpl extends EC2BaseService implements EC2Start {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public void process(Instance instance) {
        ec2UserClient.amazonEC2Async().startInstancesAsync(
                new StartInstancesRequest().withInstanceIds(instance.getInstanceId()),
                new AsyncHandler<StartInstancesRequest, StartInstancesResult>() {
                    @Override
                    public void onError(Exception exception) {
                        log.warn("something went wrong starting the server {}",
                                exception.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(StartInstancesRequest request, StartInstancesResult result) {

                    }
                });
    }

}
