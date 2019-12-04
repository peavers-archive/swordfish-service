/* Licensed under Apache-2.0 */
package space.swordfish.edge.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"space.swordfish.common.*"})
@ComponentScan({"space.swordfish.edge.*"})
@SpringBootApplication
@EnableZuulProxy
@EnableHystrix
@EnableDiscoveryClient
public class EdgeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(EdgeServiceApplication.class, args);
  }
}
