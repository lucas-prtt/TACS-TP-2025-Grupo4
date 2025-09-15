package org.eventServerClient;

import org.ConfigManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.RegistrationDTO;
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
        String token = (String) loginInfo.get("token");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate = new RestTemplate(factory);
        this.restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().setBearerAuth(token);
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        }));
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
    public List<EventDTO> getEventsByFilters(String filters, Integer page, Integer limit){
        String url = getBaseUri() + "/events" + filters + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
    }
    public String postRegistration(UUID eventID, UUID userID) throws RestClientResponseException {
        String url = getBaseUri() + "/events/registration";
        RegistrationDTO reg = new RegistrationDTO();
        reg.setAccountId(userID);
        reg.setEventId(eventID);
        return restTemplate.postForObject(url, reg, String.class);
    }
    public EventDTO getEvent(UUID uuid){
        String url = getBaseUri() + "/events/" + uuid.toString();
        return restTemplate.getForObject(url, EventDTO.class);
    }
    public List<RegistrationDTO> getRegisteredRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=CONFIRMED" + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getWaitlistRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=WAITLIST"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getCanceledRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=CANCELED"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public List<RegistrationDTO> getAllRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations"+ "?page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public void cancelRegistration(UUID userUuid, UUID registrationID){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations/" + registrationID.toString();
        restTemplate.delete(url);
    }
    public EventDTO postEvent(EventDTO eventDTO){
        String url = getBaseUri() + "/events";
        return restTemplate.postForObject(url, eventDTO, EventDTO.class);
    }
    public List<EventDTO> getEventsOrganizedBy(UUID organizerId, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + organizerId.toString() + "/organized-events" + "?page=" + page + "&limit=" + limit;
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
