package org.model.accounts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "roles")
@Getter
@Setter
public class Role {
    @Id
    private UUID id;
    private String name; // "ROLE_USER", "ROLE_ADMIN"

    public Role(String name) {
    this.name = name;
  }
}
