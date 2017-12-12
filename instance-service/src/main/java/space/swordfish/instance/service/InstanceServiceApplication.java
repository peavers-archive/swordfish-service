package space.swordfish.instance.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "space.swordfish.common.*" })
@SpringBootApplication
public class InstanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstanceServiceApplication.class, args);
	}
}
