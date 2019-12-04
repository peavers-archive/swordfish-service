/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import com.amazonaws.services.ec2.AmazonEC2Async;
import space.swordfish.common.auth.domain.User;

public interface EC2UserClient {

  AmazonEC2Async amazonEC2Async();

  AmazonEC2Async amazonEC2Async(User user);
}
