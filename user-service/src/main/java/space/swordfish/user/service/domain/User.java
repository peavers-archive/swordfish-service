package space.swordfish.user.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@Type("users")
public class User {
    @Id
    @com.github.jasminb.jsonapi.annotations.Id
    String id;
    String auth0Id;
    String requestToken;

    String displayName;
    String pictureUrl;
    String email;

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
