package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.AmazonEC2Async;
import space.swordfish.instance.service.domain.Instance;

public interface EC2Stop {

    void process(AmazonEC2Async amazonEC2Async, Instance instance);

    void process(Instance instance);

}
