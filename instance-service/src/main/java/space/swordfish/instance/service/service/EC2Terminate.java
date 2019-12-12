/* Licensed under Apache-2.0 */
package space.swordfish.instance.service.service;

import space.swordfish.instance.service.domain.Instance;

public interface EC2Terminate {

  void process(Instance instance);
}
