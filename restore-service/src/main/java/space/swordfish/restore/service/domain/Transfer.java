package space.swordfish.restore.service.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

@Type("transfers")
@Data
@NoArgsConstructor
public class Transfer {

    @Id
    String id;
    String status;

    @Relationship(value = "snapshot")
    Snapshot snapshot;
}
