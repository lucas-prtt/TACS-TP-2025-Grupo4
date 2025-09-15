package org.eventServerClient;

import org.ConfigManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.RegistrationStateDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ApiClient {

    private final RestTemplate restTemplate;

    public ApiClient(Map<String, Object> loginInfo) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate = new RestTemplate(factory);
        if(loginInfo != null){
            String token = (String) loginInfo.get("token");
            this.restTemplate.setInterceptors(List.of((request, body, execution) -> {
                request.getHeaders().setBearerAuth(token);
                request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return execution.execute(request, body);
            }));
        };
    }
    public static ApiClient withoutToken(){
        return new ApiClient(null);
    }
    public static ApiClient fromToken(String token) {
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("token", token);
        return new ApiClient(loginInfo);
    }

    public String getBaseUri(){
        return "http://"+ ConfigManager.getInstance().get("server.ip")+":"+ConfigManager.getInstance().get("server.port");
    }

    public AccountDTO postAccount(String username, String password){
        String url = getBaseUri() + "/auth/register";
        return restTemplate.postForObject(url, new AccountDTO(username, password), AccountDTO.class);
    }
    public Map<String, Object> loginOneTimeCode(String oneTimeCode){
        String url = getBaseUri() + "/auth/oneTimeCode";
        return restTemplate.getForObject(url, Map.class);
    }
    public Map<String, Object> loginUserAndPassword(AccountDTO accountDTO){
        String url = getBaseUri() + "/auth/oneTimeCode";
        return restTemplate.postForObject(url, accountDTO, Map.class);
    }
    public List<EventDTO> getEventsByFilters(String filters, Integer page, Integer limit){
        String url = getBaseUri() + "/events" + filters + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
    }
    public String postRegistration(UUID eventID) throws RestClientResponseException {
        String url = getBaseUri() + "/events/"+eventID+"/registrations";
        return restTemplate.postForObject(url, null, String.class);
    }
    public EventDTO getEvent(UUID uuid){
        String url = getBaseUri() + "/events/" + uuid.toString();
        return restTemplate.getForObject(url, EventDTO.class);
    }
    public List<RegistrationDTO> getRegisteredRegistrations(Integer page, Integer limit){
        String url = getBaseUri() + "/registrations?registrationState=CONFIRMED" + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getWaitlistRegistrations(Integer page, Integer limit){
        String url = getBaseUri() + "/registrations?registrationState=WAITLIST"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getCanceledRegistrations(Integer page, Integer limit){
        String url = getBaseUri() + "/registrations?registrationState=CANCELED"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getAllRegistrations(Integer page, Integer limit){
        String url = getBaseUri() + "/registrations"+ "?page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public void cancelRegistration(UUID registrationID){
        String url = getBaseUri() +"/registrations/" + registrationID.toString();
        RegistrationDTO reg = new RegistrationDTO();
        reg.setState(RegistrationStateDTO.CANCELED);
        restTemplate.patchForObject(url, reg, Void.class);
    }
    public EventDTO postEvent(EventDTO eventDTO){
        String url = getBaseUri() + "/events";
        return restTemplate.postForObject(url, eventDTO, EventDTO.class);
    }

    public List<EventDTO> getEventsOrganizedBy(Integer page, Integer limit){
        String url = getBaseUri() + "events/organized-events" + "?page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
    }
    public EventDTO patchEvent(UUID idEvent, EventDTO eventDTOPatch){
        String url = getBaseUri() + "/events/" + idEvent;
        return restTemplate.patchForObject(url, eventDTOPatch, EventDTO.class);
    }
    public EventDTO patchEventState(UUID idEvent, EventStateDTO newState){
        EventDTO eventDTOPatch = new EventDTO();
        eventDTOPatch.setState(newState);
        return patchEvent(idEvent, eventDTOPatch);
    }


}
