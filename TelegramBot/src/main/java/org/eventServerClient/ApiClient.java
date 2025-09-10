package org.eventServerClient;

import org.ConfigManager;
import org.eventServerClient.dtos.PostUsuarioDTO;
import org.springframework.web.client.RestTemplate;

public class ApiClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static String getBaseUri(){
        return "http://"+ ConfigManager.getInstance().get("server.ip")+":"+ConfigManager.getInstance().get("server.port");
    }

    public static PostUsuarioDTO postUsuario(String username){
        String url = getBaseUri() + "/accounts";
        return restTemplate.postForObject(url, new PostUsuarioDTO(username), PostUsuarioDTO.class);
    }

}
