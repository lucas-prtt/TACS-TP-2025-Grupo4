package org.eventServerClient;

import org.ConfigManager;
import org.eventServerClient.dtos.AccountDTO;
import org.springframework.web.client.RestTemplate;

public class ApiClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static String getBaseUri(){
        return "http://"+ ConfigManager.getInstance().get("server.ip")+":"+ConfigManager.getInstance().get("server.port");
    }

    public static AccountDTO postAccount(String username){
        String url = getBaseUri() + "/accounts";
        return restTemplate.postForObject(url, new AccountDTO(username), AccountDTO.class);
    }
    public static AccountDTO getAccountByUsername(String username){
        String url = getBaseUri() + "/accounts/usernames/"+username;
        return restTemplate.getForObject(url, AccountDTO.class);
    }
}
