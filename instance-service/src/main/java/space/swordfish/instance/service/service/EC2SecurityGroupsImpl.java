/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EC2SecurityGroupsImpl extends EC2BaseService implements EC2SecurityGroups {

  @Autowired private EC2UserClient ec2UserClient;

  @Override
  public List<space.swordfish.instance.service.domain.SecurityGroup> getAll() {
    Future<DescribeSecurityGroupsResult> describeSecurityGroupsResultFuture =
        ec2UserClient.amazonEC2Async().describeSecurityGroupsAsync();

    while (!describeSecurityGroupsResultFuture.isDone()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      List<space.swordfish.instance.service.domain.SecurityGroup> results = new ArrayList<>();

      DescribeSecurityGroupsResult describeSecurityGroupsResult =
          describeSecurityGroupsResultFuture.get();
      List<SecurityGroup> securityGroups = describeSecurityGroupsResult.getSecurityGroups();
      for (SecurityGroup securityGroup : securityGroups) {
        space.swordfish.instance.service.domain.SecurityGroup localGroup =
            new space.swordfish.instance.service.domain.SecurityGroup();
        localGroup.setGroupId(securityGroup.getGroupId());
        localGroup.setDescription(securityGroup.getDescription());
        localGroup.setGroupName(securityGroup.getGroupName());

        results.add(localGroup);
      }

      return results;

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    return null;
  }
}
