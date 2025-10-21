package org.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.menus.userMenu.UserMenu;
import org.springframework.web.client.HttpClientErrorException;
import org.users.TelegramUser;

import java.net.http.HttpClient;
import java.util.Map;

public class ErrorHandler {
public static String getErrorMessage(HttpClientErrorException e, TelegramUser user){
    try {
        Map<String, String> errorMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
        return e.getStatusCode().toString()+"\n" + "⚠️ " + errorMap.getOrDefault("error", user.getLocalizedMessage("unknownErrorInServer")) + "\n\n";
    } catch (Exception e2) {
        System.err.println(e2.getMessage());
        return "⚠️ " + user.getLocalizedMessage("unknownErrorInServer");
    }
}

}
