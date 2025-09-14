package org.DTOs.accounts;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.model.accounts.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
  UUID uuid;
  String username;

  public static AccountResponseDTO toAccountResponseDTO(Account account){
      return new AccountResponseDTO(
        account.getId(),
        account.getUsername()
      );
  }

}



