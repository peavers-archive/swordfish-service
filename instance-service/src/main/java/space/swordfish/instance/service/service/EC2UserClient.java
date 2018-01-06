package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.AmazonEC2Async;

public interface EC2UserClient {

    AmazonEC2Async amazonEC2Async();

//    AmazonEC2Async amazonEC2Async(String userId);
}