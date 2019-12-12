/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.domain;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Represents one instance with data both from AWS and Swordfish users. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@Type("instances")
public class Instance {

  @Id @com.github.jasminb.jsonapi.annotations.Id String id;

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

  Date lastSync;

  @Relationship("securityGroups")
  List<SecurityGroup> securityGroups;
}
