/* Licensed under Apache-2.0 */
package space.swordfish.node.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"space.swordfish.common.*"})
@ComponentScan({"space.swordfish.node.*"})
@SpringBootApplication
public class NodeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(NodeServiceApplication.class, args);
  }
}
