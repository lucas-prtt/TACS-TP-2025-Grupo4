package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccountDTO {
    private String username;
    private String password;
    private String uuid;
    public AccountDTO(String username, String password){
        this.username = username;
        this.password = password;
    }
}
