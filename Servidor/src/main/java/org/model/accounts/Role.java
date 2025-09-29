package org.model.accounts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Getter
@Setter
public class Role {
  private String name; // "ROLE_USER", "ROLE_ADMIN"

  public Role(String name) {
    this.name = name;
  }
}
