package org.eventServerClient.dtos.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.users.TelegramUser;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class CategoryDTO {
    String title;

    public String toShortString(){
        return title;
    }

}
