package space.swordfish.instance.service.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.common.auth.domain.User;
import space.swordfish.common.auth.services.AuthenticationService;

@Slf4j
@Service
public class EC2UserClientImpl extends EC2BaseService implements EC2UserClient {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public AmazonEC2Async amazonEC2Async() {
        User user = authenticationService.getCurrentUser();

        String accessKey = user.getAwsKey();
        String secretKey = user.getAwsSecret();
        String region = user.getAwsRegion();

        if (secretKey.equals("") || accessKey.equals("") || region.equals("")) {
            return null;
        }

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                accessKey, secretKey);

        return AmazonEC2AsyncClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

    @Override
    public AmazonEC2Async amazonEC2Async(User user) {
        String accessKey = user.getAwsKey();
        String secretKey = user.getAwsSecret();
        String region = user.getAwsRegion();

        if (secretKey.equals("") || accessKey.equals("") || region.equals("")) {
            return null;
        }

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                accessKey, secretKey);

        return AmazonEC2AsyncClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

}
