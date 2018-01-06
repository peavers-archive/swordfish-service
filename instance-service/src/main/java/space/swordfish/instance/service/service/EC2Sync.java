package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.AmazonEC2Async;
import space.swordfish.instance.service.domain.Instance;

public interface EC2Sync {

    /**
     * Sync all Instance data down from AWS
     *
     * @param amazonEC2Async {@link AmazonEC2Async} set with required credentials
     * @return Iterable list of all instances and their latest data
     */
    Iterable<Instance> syncAll(AmazonEC2Async amazonEC2Async);

    /**
     * Sync all Instance data down from AWS. Uses the current logged in users credentials.
     *
     * @return Iterable list of all instances and their latest data
     */
    Iterable<Instance> syncAll();

    /**
     * Sync a single instance down from AWS
     *
     * @param instance Instance object, must have instanceId set.
     * @return Instance data from AWS.
     */
    Instance syncByInstance(Instance instance);
}
