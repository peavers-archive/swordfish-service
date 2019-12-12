/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.KeyPair;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.instance.service.domain.Instance;

@Service
public class EC2KeyPairImpl extends EC2BaseService implements EC2KeyPair {

  @Autowired private EC2UserClient ec2UserClient;

  @Override
  public String create(Instance instance) {
    CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
    createKeyPairRequest.withKeyName(instance.getKeyName());
    CreateKeyPairResult createKeyPairResult =
        ec2UserClient.amazonEC2Async().createKeyPair(createKeyPairRequest);

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

    ec2UserClient.amazonEC2Async().deleteKeyPair(deleteKeyPairRequest);
  }
}
