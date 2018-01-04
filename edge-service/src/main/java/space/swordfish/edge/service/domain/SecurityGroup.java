package space.swordfish.edge.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Represents one instance with data both from AWS and Swordfish users.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("security-groups")
public class SecurityGroup {

    @com.github.jasminb.jsonapi.annotations.Id
    String id = UUID.randomUUID().toString();

    String ownerId;
    String groupName;
    String groupId;
    String description;
    String ipPermissions;
    String ipPermissionsEgress;
    String vpcId;

    @Relationship("instances")
    List<Instance> instances;
}
