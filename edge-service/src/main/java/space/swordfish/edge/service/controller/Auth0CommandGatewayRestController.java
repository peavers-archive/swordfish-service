package space.swordfish.edge.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.edge.service.domain.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This is experimental, will need to be moved to a user-service at some point.
 */
@Slf4j
@RestController
public class Auth0CommandGatewayRestController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Auth0Service auth0Service;


    @PostMapping("/users")
    public ResponseEntity<String> users(@RequestBody String payload) {
        User user = readUserPayload(payload);

        if (user == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("aws_key", user.getAwsKey());
        data.put("aws_secret", user.getAwsSecret());
        data.put("aws_region", user.getAwsRegion());

        data.put("silverstripe_username", user.getSilverstripeUsername());
        data.put("silverstripe_token", user.getSilverstripeToken());

        data.put("gitlab_username", user.getGitlabUsername());
        data.put("gitlab_password", user.getGitlabPassword());

        auth0Service.setUserMetaData(authenticationService.getCurrentAuth0User().getId(), data);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private User readUserPayload(String payload) {
        try {
            String result = java.net.URLDecoder.decode(payload, "UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(result, User.class);
        } catch (Exception e) {
            log.error("Failed updating picture {}", e.getLocalizedMessage());
        }
        return null;
    }

}
