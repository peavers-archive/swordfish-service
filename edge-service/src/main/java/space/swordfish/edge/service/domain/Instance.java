package space.swordfish.edge.service.domain;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Tag;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.Data;

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
