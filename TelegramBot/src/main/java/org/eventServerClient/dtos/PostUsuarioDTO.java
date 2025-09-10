package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostUsuarioDTO {
    private String username;
    private String uuid;
    public PostUsuarioDTO(String username){
        this.username = username;
    }
}
