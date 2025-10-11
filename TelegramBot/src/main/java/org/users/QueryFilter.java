package org.users;

import lombok.Data;

@Data
public class QueryFilter {
    private String type;
    private String value;
    public QueryFilter(String type){
        this.setType(type);
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
