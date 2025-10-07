package org.model.accounts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    private UUID id;
    private String name; // "ROLE_USER", "ROLE_ADMIN"

  /**
   * Constructor que inicializa el rol con el nombre especificado.
   * @param name Nombre del rol (por ejemplo, "ROLE_USER", "ROLE_ADMIN")
   */
  public Role(String name) {
    this.name = name;
  }
}
