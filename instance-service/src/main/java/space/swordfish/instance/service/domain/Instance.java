package space.swordfish.instance.service.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one instance with data both from AWS and Swordfish users.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@Type("instances")
public class Instance {

	@Id
	@com.github.jasminb.jsonapi.annotations.Id
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
