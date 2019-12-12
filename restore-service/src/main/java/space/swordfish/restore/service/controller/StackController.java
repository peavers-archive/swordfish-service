/* Licensed under Apache-2.0 */
package space.swordfish.restore.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.swordfish.restore.service.api.stack.SilverstripeStack;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/stacks")
public class StackController {

  private final SilverstripeStack silverstripeStack;

  @Autowired
  public StackController(SilverstripeStack silverstripeStack) {
    this.silverstripeStack = silverstripeStack;
  }

  @GetMapping()
  public ResponseEntity<JsonNode> listAll() {
    return silverstripeStack.listAll();
  }

  @GetMapping("{projectId}")
  public ResponseEntity<JsonNode> view(@PathVariable String projectId) {
    return silverstripeStack.view(projectId);
  }
}
