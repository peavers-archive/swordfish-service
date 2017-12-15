package space.swordfish.edge.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import space.swordfish.edge.service.service.AuthenticationService;

@Api(tags = "Restore Query")
@RestController
public class RestoreQueryGatewayRestController {

	private final static String SERVICE = "http://restore-service";

	private final RestTemplate restTemplate;
	private final AuthenticationService authenticationService;

	@Autowired
	public RestoreQueryGatewayRestController(RestTemplate restTemplate,
			AuthenticationService authenticationService) {
		this.restTemplate = restTemplate;

		this.authenticationService = authenticationService;
	}

	@ApiOperation(value = "Returns a list of deployments associated to the environment.")
	@GetMapping("/restore/deployments/{projectId}/{environmentId}")
	public ResponseEntity<String> listAllDeployments(@PathVariable String projectId,
			@PathVariable String environmentId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/deployments/{projectId}/{environmentId}",
				HttpMethod.GET, null, reference, projectId, environmentId);
	}

	@ApiOperation(value = "Obtain information about a deployment.")
	@GetMapping("/restore/deployments/{projectId}/{environmentId}/{deploymentId}")
	public ResponseEntity<String> viewDeployments(@PathVariable String projectId,
			@PathVariable String environmentId, @PathVariable String deploymentId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(
				SERVICE + "/deployments/{projectId}/{environmentId}/{deploymentId}",
				HttpMethod.GET, null, reference, projectId, environmentId, deploymentId);
	}

	@ApiOperation(value = "Return a list of all stacks that you have the authority to view, with relevant datapoints.")
	@GetMapping("/stacks")
	public ResponseEntity<String> listAllStacks(
			@RequestHeader("Authorization") String token) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/stacks/", HttpMethod.GET,
				authenticationService.addAuthenticationHeader(), reference);
	}

	@ApiOperation(value = "View information about a specific stack.")
	@GetMapping("/restore/stacks/{projectId}")
	public ResponseEntity<String> viewStacks(@PathVariable String projectId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/stacks/{projectId}", HttpMethod.GET,
				null, reference, projectId);
	}

	@ApiOperation(value = "Returns a list of snapshots associated with a stack. ")
	@GetMapping("/restore/snapshots/{projectId}")
	public ResponseEntity<String> listAllSnapshots(@PathVariable String projectId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/snapshots/{projectId}", HttpMethod.GET,
				null, reference, projectId);
	}

	@ApiOperation(value = "Obtain information about a snapshot.")
	@GetMapping("/restore/snapshots/{projectId}/{snapshotId}")
	public ResponseEntity<String> viewSnapshots(@PathVariable String projectId,
			@PathVariable String snapshotId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/snapshots/{projectId}/{snapshotId}",
				HttpMethod.GET, null, reference, projectId, snapshotId);
	}

	@ApiOperation(value = "Obtain information about a transfer.")
	@GetMapping("/restore/snapshots/transfer/{projectId}/{transferId}")
	public ResponseEntity<String> transferSnapshots(@PathVariable String projectId,
			@PathVariable String transferId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(
				SERVICE + "/snapshots/transfer/{projectId}/{transferId}", HttpMethod.GET,
				null, reference, projectId, transferId);
	}

	@ApiOperation(value = "View status of the code fetch.")
	@GetMapping("/restore/git/{projectId}/{fetchId}")
	public ResponseEntity<String> viewFetch(@PathVariable("projectId") String projectId,
			@PathVariable("fetchId") String fetchId) {
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
		};

		return restTemplate.exchange(SERVICE + "/restore/git/{projectId}/{fetchId}",
				HttpMethod.GET, null, reference, projectId, fetchId);
	}
}