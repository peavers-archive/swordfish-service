package space.swordfish.node.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class NodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeServiceApplication.class, args);
    }
}