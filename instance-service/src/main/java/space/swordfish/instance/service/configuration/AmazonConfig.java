package space.swordfish.instance.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
public class AmazonConfig {

	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	@Bean
	@Primary
	public AmazonEC2Async amazonEC2Async() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey,
				secretKey);

		return AmazonEC2AsyncClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey,
				secretKey);

		return AmazonSQSAsyncClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}

	@Bean
	@Primary
	public QueueMessagingTemplate queueMessagingTemplate() {
		return new QueueMessagingTemplate(amazonSQSAsync());
	}

}
