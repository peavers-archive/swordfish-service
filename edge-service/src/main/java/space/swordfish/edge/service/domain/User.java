package space.swordfish.edge.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

@Type("users")
@Data
public class User {
    @Id
    String id;
    String auth0Id;
    String requestToken;

    String awsKey;
    String awsSecret;
    String awsRegion;

    String silverstripeUsername;
    String silverstripeToken;

    String gitlabUsername;
    String gitlabPassword;

    String swordfishCommand;

    @Relationship("team")
    Team team;
}
