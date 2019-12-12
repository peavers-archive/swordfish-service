/* Licensed under Apache-2.0 */
package space.swordfish.restore.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"space.swordfish.common.*"})
@ComponentScan({"space.swordfish.restore.*"})
@EnableDiscoveryClient
@SpringBootApplication
public class RestoreServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(RestoreServiceApplication.class, args);
  }
}
