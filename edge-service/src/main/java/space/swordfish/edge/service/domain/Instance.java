package space.swordfish.edge.service.domain;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Type("instances")
public class Instance {

    @Id
    String id;

    Date created;
    List<Tag> tags;
    List<GroupIdentifier> securityGroupIds;

    boolean production;
    boolean staticIp;
    boolean swordfish;

    String name;
    String description;
    String instanceType;
    String imageId;
    String keyName;
    String keyBlob;
    String subnetId;
    String instanceId;
    String state;
    String publicIp;
    String privateIp;
    String securityGroupId;

    String swordfishCommand;

    String userToken;
    String userId;
    String userName;
    String userPicture;

    @Relationship("securityGroups")
    List<SecurityGroup> securityGroups;

}

