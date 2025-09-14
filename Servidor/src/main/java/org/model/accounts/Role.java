package org.model.accounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {
  private String name; // "ROLE_USER", "ROLE_ADMIN"

  public Role(String name) {
    this.name = name;
  }
}
