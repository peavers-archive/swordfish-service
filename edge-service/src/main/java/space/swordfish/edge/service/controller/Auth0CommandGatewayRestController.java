package space.swordfish.edge.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import space.swordfish.common.auth.services.Auth0Service;
import space.swordfish.common.auth.services.AuthenticationService;
import space.swordfish.edge.service.domain.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class Auth0CommandGatewayRestController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Auth0Service auth0Service;

    @PostMapping("/users/picture")
    public ResponseEntity<String> picture(@RequestBody String payload) {
        try {
            User user = readUserPayload(payload);
            com.auth0.json.mgmt.users.User auth0User = getBlankAuth0User();

            Map<String, Object> data = new HashMap<>();
            data.put("picture", user.getPicture());

            auth0User.setUserMetadata(data);

            auth0Service.updateUser(auth0Service.getUserId(authenticationService.getCurrentToken()), auth0User);
        } catch (Exception e) {
            log.error("Failed updating picture {}", e.getLocalizedMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/gitlab")
    public ResponseEntity<String> gitlab(@RequestBody String payload) {
        try {
            User user = readUserPayload(payload);
            com.auth0.json.mgmt.users.User auth0User = getBlankAuth0User();

            Map<String, Object> data = new HashMap<>();
            data.put("gitlab_username", user.getGitlabUsername());
            data.put("gitlab_password", encryptPassword(user.getGitlabPassword()));

            auth0User.setUserMetadata(data);

            auth0Service.updateUser(auth0Service.getUserId(authenticationService.getCurrentToken()), auth0User);
        } catch (Exception e) {
            log.error("Failed gitlab picture {}", e.getLocalizedMessage());
        }
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

    private com.auth0.json.mgmt.users.User getBlankAuth0User() {
        com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User(null);

        return auth0User;
    }

    private String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

}
