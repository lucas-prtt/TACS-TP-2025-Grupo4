package org.eventServerClient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {
    private String username;
    private String uuid;
    public AccountDTO(String username){
        this.username = username;
    }
}
