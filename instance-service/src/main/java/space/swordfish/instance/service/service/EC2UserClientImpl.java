package space.swordfish.instance.service.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.instance.service.domain.User;

@Slf4j
@Service
public class EC2UserClientImpl extends EC2BaseService implements EC2UserClient {

    @LoadBalanced
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Override
    public AmazonEC2Async amazonEC2Async() {
        User user = getUser();

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

    private User getUser() {
        String userId = auth0Service.getUserIdFromToken(authenticationService.getCurrentAuth0Token());
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        ResponseEntity<String> exchange = restTemplate.exchange("http://user-service/users/{id}", HttpMethod.GET, authenticationService.addAuthenticationHeader(), reference, userId);

        return jsonTransformService.read(User.class, exchange.getBody());
    }

}
