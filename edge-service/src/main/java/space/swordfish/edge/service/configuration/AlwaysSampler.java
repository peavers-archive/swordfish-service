package space.swordfish.edge.service.configuration;


import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Sampler {

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

}