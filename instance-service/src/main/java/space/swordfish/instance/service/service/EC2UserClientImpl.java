package space.swordfish.instance.service.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.auth.services.AuthenticationService;

@Slf4j
@Service
public class EC2UserClientImpl implements EC2UserClient {

    @Autowired
    private Auth0Service auth0Service;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public AmazonEC2Async amazonEC2Async() {
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
    public AmazonEC2Async amazonEC2Async(String token) {
        String userId = auth0Service.getUserIdFromToken(token);
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
