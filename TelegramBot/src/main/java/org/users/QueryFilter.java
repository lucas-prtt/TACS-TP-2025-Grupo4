package org.users;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class QueryFilter {
    private String type;
    private String value;
    public QueryFilter(String type){
        this.setType(type);
    }
    public QueryFilter(String type, String value){
        this.setType(type);
        this.setValue(value);
    }
    public String toString(){
        return type + "=" + value;
    }
    public String toLocalizedString(TelegramUser user){
        return user.getLocalizedMessage(type) + " = " + value;
    }
    public String getTypeLocalized(TelegramUser user){
        return user.getLocalizedMessage(type);
    }
}
