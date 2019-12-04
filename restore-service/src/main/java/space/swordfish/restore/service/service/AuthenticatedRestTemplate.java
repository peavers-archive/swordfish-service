/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.service;

import org.springframework.web.client.RestTemplate;

public interface AuthenticatedRestTemplate {

  RestTemplate restTemplate();
}
