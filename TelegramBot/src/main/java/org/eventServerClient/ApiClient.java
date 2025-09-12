package org.eventServerClient;

import org.ConfigManager;
import org.eventServerClient.dtos.AccountDTO;
import org.eventServerClient.dtos.RegistrationDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public static List<EventDTO> getEventsByFilters(String filters, Integer page, Integer limit){
        String url = getBaseUri() + "/events" + filters + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
    }
    public static String postRegistration(UUID eventID, UUID userID) throws RestClientResponseException {
        String url = getBaseUri() + "/events/registration";
        RegistrationDTO reg = new RegistrationDTO();
        reg.setAccountId(userID);
        reg.setEventId(eventID);
        return restTemplate.postForObject(url, reg, String.class);
    }
    public static EventDTO getEvent(UUID uuid){
        String url = getBaseUri() + "/events/" + uuid.toString();
        return restTemplate.getForObject(url, EventDTO.class);
    }
    public static List<RegistrationDTO> getRegisteredRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=CONFIRMED" + "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public static List<RegistrationDTO> getWaitlistRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=WAITLIST"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public static List<RegistrationDTO> getCanceledRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations?registrationState=CANCELED"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public static List<RegistrationDTO> getAllRegistrations(UUID userUuid, Integer page, Integer limit){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations"+ "&page=" + page + "&limit=" + limit;
        return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
    }
    public static void cancelRegistration(UUID userUuid, UUID registrationID){
        String url = getBaseUri() + "/accounts/" + userUuid.toString()+"/registrations/" + registrationID.toString();
        restTemplate.delete(url);
    }
    public static EventDTO postEvent(EventDTO eventDTO){
        String url = getBaseUri() + "/events";
        return restTemplate.postForObject(url, eventDTO, EventDTO.class);
    };

}
