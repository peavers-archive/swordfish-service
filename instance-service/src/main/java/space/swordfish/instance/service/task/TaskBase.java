package space.swordfish.instance.service.task;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import com.auth0.json.mgmt.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.instance.service.repository.InstanceRepository;

abstract class TaskBase {

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    NotificationService notificationService;

    @Autowired
    JsonTransformService jsonTransformService;

    @Autowired
    Auth0Service auth0Service;

    @Autowired
    InstanceRepository instanceRepository;

    AmazonEC2Async customAmazonEC2Async(User user) {
        if (user.getUserMetadata() == null) {
            return null;
        }

        if (user.getUserMetadata().containsKey("aws_key") && user.getUserMetadata().containsKey("aws_secret") && user.getUserMetadata().containsKey("aws_region")) {
            String awsKey = (String) user.getUserMetadata().get("aws_key");
            String awsSecret = (String) user.getUserMetadata().get("aws_secret");
            String awsRegion = (String) user.getUserMetadata().get("aws_region");

            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                    awsKey, awsSecret);

            return AmazonEC2AsyncClientBuilder.standard().withRegion(awsRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                    .build();
        }

        return null;
    }

}
