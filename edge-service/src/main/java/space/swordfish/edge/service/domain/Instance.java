package space.swordfish.edge.service.domain;


import com.amazonaws.services.autoscaling.model.Tag;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Type("instances")
@Data
public class Instance {

    @Id
    String id;

    Date created;
    List<Tag> tags;
    List<GroupIdentifier> securityGroupIds;
    boolean production;
    boolean staticIp;

    String name;
    String description;
    String instanceType;
    String imageId;
    String keyName;
    String subnetId;
    String instanceId;
    String state;
    String publicIp;
    String privateIp;

    String swordfishCommand;

    String userToken;
    String userId;
    String userName;
    String userPicture;
}
