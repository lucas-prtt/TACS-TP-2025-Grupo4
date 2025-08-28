package org.dominio.usuarios;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class Account {
    UUID uuid;
    String username;
    String salt; //Temporales, hasta que sepamos implementarlo
    String passwordHash;    // Idem
}
