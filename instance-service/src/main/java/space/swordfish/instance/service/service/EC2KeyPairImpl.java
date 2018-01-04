package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.KeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

import java.time.Instant;

@Service
public class EC2KeyPairImpl implements EC2KeyPair {

    @Autowired
    private EC2UserClient ec2UserClient;

    @Override
    public String create(Instance instance) {
        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        createKeyPairRequest.withKeyName(instance.getKeyName());
        CreateKeyPairResult createKeyPairResult = ec2UserClient.amazonEC2Async(instance.getUserToken()).createKeyPair(createKeyPairRequest);

        KeyPair keyPair = createKeyPairResult.getKeyPair();

        return keyPair.getKeyMaterial();
    }

    @Override
    public String setName(Instance instance) {
        return "swordfish-" + instance.getName() + "-" + Instant.now().getEpochSecond();
    }

    @Override
    public void delete(Instance instance) {
        DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest();
        deleteKeyPairRequest.withKeyName(instance.getKeyName());

        ec2UserClient.amazonEC2Async(instance.getUserToken()).deleteKeyPair(deleteKeyPairRequest);
    }
}
