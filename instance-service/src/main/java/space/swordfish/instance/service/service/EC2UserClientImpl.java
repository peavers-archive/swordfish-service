package space.swordfish.instance.service.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EC2UserClientImpl extends EC2BaseService implements EC2UserClient {

    @Override
    public AmazonEC2Async amazonEC2Async() {

        if (authenticationService.getCurrentAuth0Token() == null) {
            log.error("No token set, use amazonEC2Async(String token) instead!");
        }

        String userId = auth0Service.getUserIdFromToken(authenticationService.getCurrentAuth0Token());
        String accessKey = auth0Service.getEncryptedUserMetaByKey(userId, "aws_key");
        String secretKey = auth0Service.getEncryptedUserMetaByKey(userId, "aws_secret");
        String region = auth0Service.getEncryptedUserMetaByKey(userId, "aws_region");

        // Because Auth0 replaces '+' chars with spaces, this puts it back in
        secretKey = secretKey.replaceAll(" ", "+");

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                accessKey, secretKey);

        return AmazonEC2AsyncClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

    @Override
    public AmazonEC2Async amazonEC2Async(String userId) {
        String accessKey = auth0Service.getEncryptedUserMetaByKey(userId, "aws_key");
        String secretKey = auth0Service.getEncryptedUserMetaByKey(userId, "aws_secret");
        String region = auth0Service.getEncryptedUserMetaByKey(userId, "aws_region");

        // Because Auth0 replaces '+' chars with spaces, this puts it back in
        secretKey = secretKey.replaceAll(" ", "+");

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                accessKey, secretKey);

        return AmazonEC2AsyncClientBuilder.standard().withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }
}
