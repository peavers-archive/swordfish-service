package space.swordfish.user.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@Type("users")
public class User extends BaseDomain {
    // Profile settings
    String email;
    String familyName;
    String givenName;
    String nickName;
    String name;
    String picture;
    String username;

    // Swordfish settings
    String awsKey;
    String awsSecret;
    String awsRegion;
    String silverstripeUsername;
    String silverstripeToken;
    String gitlabUsername;
    String gitlabPassword;

    // Team settings
    @Relationship("team")
    Team team;
    String teamId;
    String teamState = "none";
}
