package space.swordfish.edge.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

import java.util.UUID;

@Type("users")
@Data
public class User {

    @Id
    String id = UUID.randomUUID().toString();

    String picture;
    String gitlabUsername;
    String gitlabPassword;

    String awsKey;
    String awsSecret;
    String awsRegion;
}
