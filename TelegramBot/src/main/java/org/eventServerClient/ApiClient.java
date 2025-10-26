package org.eventServerClient;

import lombok.Setter;
import org.ConfigManager;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.eventServerClient.dtos.*;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.EventStateDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.users.TelegramUser;

import java.util.*;
import java.util.stream.Stream;

public class ApiClient {

    private final RestTemplate restTemplate;
    private final TelegramUser user;
    public ApiClient(String token, TelegramUser user) {
        this.user = user;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate = new RestTemplate(factory);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.ACCEPT_LANGUAGE, user.getLang());
            return execution.execute(request, body);
        });

        if(token != null){
            interceptors.add(
                (request, body, execution) ->
                    {
                        request.getHeaders().setBearerAuth(token);
                        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        return execution.execute(request, body);
                    }
            );
        };

        restTemplate.setInterceptors(interceptors);
    }
    public static ApiClient withoutToken(TelegramUser user){
        return new ApiClient(null, user);
    }
    public static ApiClient fromToken(String token, TelegramUser user) {
        return new ApiClient(token, user);
    }

    public String getBaseUri(){
            return "http://"+ ConfigManager.getInstance().get("server.ip")+":"+ConfigManager.getInstance().get("server.port");
    }

    public AccountDTO postAccount(String username, String password){
        try {
        String url = getBaseUri() + "/auth/register";
        return restTemplate.postForObject(url, new AccountDTO(username, password), AccountDTO.class);
        }catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                user.deleteCurrentAccount();
            }
            throw e;
        }
    }
    public Map<String, Object> loginOneTimeCode(String oneTimeCode, String username){
        try {
            String url = getBaseUri() + "/auth/oneTimeCode" + "?username=" + username + "&code=" + oneTimeCode;
            return restTemplate.getForObject(url, Map.class);
        }catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                user.deleteCurrentAccount();
            }
            throw e;
        }
    }

    public Map<String, Object> loginUserAndPassword(LoginRequestDTO accountDTO){
        try {
            String url = getBaseUri() + "/auth/login";
            return restTemplate.postForObject(url, accountDTO, Map.class);
        }catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public List<EventDTO> getEventsByFilters(String filters, Integer page, Integer limit){
        try {
            String url = getBaseUri() + "/events" + filters + "&page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
        }catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }
    public RegistrationDTO postRegistration(UUID eventID) throws RestClientResponseException {
        try {
            String url = getBaseUri() + "/events/" + eventID + "/registrations";
            return restTemplate.postForObject(url, null, RegistrationDTO.class);
        }catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public EventDTO getEvent(UUID uuid){
        try{
            String url = getBaseUri() + "/events/" + uuid.toString();
            return restTemplate.getForObject(url, EventDTO.class);
        }
        catch (HttpClientErrorException e){
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
            user.deleteCurrentAccount();
        throw e;
    }

    }

    public List<RegistrationDTO> getRegisteredRegistrations(Integer page, Integer limit){
        try {
            String url = getBaseUri() + "/registrations?registrationState=CONFIRMED" + "&page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }


    public List<RegistrationDTO> getWaitlistRegistrations(Integer page, Integer limit){
        try{
            String url = getBaseUri() + "/registrations?registrationState=WAITLIST"+ "&page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }


    public List<RegistrationDTO> getCanceledRegistrations(Integer page, Integer limit){
        try {
            String url = getBaseUri() + "/registrations?registrationState=CANCELED" + "&page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
        } catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public List<RegistrationDTO> getAllRegistrations(Integer page, Integer limit){
        try {
            String url = getBaseUri() + "/registrations" + "?page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, RegistrationDTO[].class)));
        } catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public void cancelRegistration(UUID registrationID){
        try {
            String url = getBaseUri() + "/registrations/" + registrationID.toString();
            RegistrationDTO reg = new RegistrationDTO();
            reg.setState(RegistrationStateDTO.CANCELED);
            restTemplate.patchForObject(url, reg, Void.class);
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public EventDTO postEvent(EventDTO eventDTO){
        try {
            String url = getBaseUri() + "/events";
            return restTemplate.postForObject(url, eventDTO, EventDTO.class);
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public List<EventDTO> getEventsOrganizedBy(Integer page, Integer limit){
        try {
            String url = getBaseUri() + "/events/organized-events" + "?page=" + page + "&limit=" + limit;
            return List.of(Objects.requireNonNull(restTemplate.getForObject(url, EventDTO[].class)));
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }
    public EventDTO patchEvent(UUID idEvent, EventDTO eventDTOPatch){
        try{
            String url = getBaseUri() + "/events/" + idEvent;
            return restTemplate.patchForObject(url, eventDTOPatch, EventDTO.class);
        }catch (HttpClientErrorException e){
        if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
            user.deleteCurrentAccount();
        }
        throw e;
    }
    }
    public EventDTO patchEventState(UUID idEvent, EventStateDTO newState){
       try {
        EventDTO eventDTOPatch = new EventDTO();
        eventDTOPatch.setState(newState);
        return patchEvent(idEvent, eventDTOPatch);
        }catch (HttpClientErrorException e){
        if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
            user.deleteCurrentAccount();
        }
        throw e;
    }
    }
    public StatsDTO getAdminStats(){
        try {
            String url = getBaseUri() + "/admin/stats";
            return restTemplate.getForObject(url, StatsDTO.class);
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }

    public List<CategoryDTO> getCategories(Integer page, Integer limit, String startsWith){
        try {
                List<CategoryDTO> mockCategories = Stream.of(
                    "Concierto", "Venta", "Curso", "Deporte", "Ceremonia",
                    "Arte", "Ciencia", "Competencia", "Teatro", "Taller",
                    "Exposición", "Seminario", "Conferencia", "Festival", "Viaje",
                    "Campamento", "Deporte Extremo", "Música", "Danza", "Literatura",
                    "Fotografía", "Cine", "Cultura", "Educación", "Medicina",
                    "Tecnología", "Gastronomía", "Networking", "Voluntariado", "Negocios",
                    "Religión", "Yoga", "Meditación", "Idiomas", "Programación",
                    "Historia", "Filosofía", "Astronomía", "Robótica", "Ecología",
                    "Moda", "Belleza", "Fitness", "Marketing", "Psicología",
                    "Arquitectura", "Diseño", "Videojuegos", "Viajes de Aventura", "Emprendimiento"
            ).map(CategoryDTO::new).toList();
            mockCategories = mockCategories.stream().filter(c -> c.getTitle().toLowerCase().startsWith(startsWith.toLowerCase())).toList();
            int fromIndex = (page) * limit;
            int toIndex = Math.min(fromIndex + limit, mockCategories.size());
            if (fromIndex >= mockCategories.size()) {
                return List.of();
            }
            return mockCategories.subList(fromIndex, toIndex);
        }catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                user.deleteCurrentAccount();
            throw e;
        }
    }
    public CategoryDTO postCategory(CategoryDTO categoryDTO){
        try {
        CategoryDTO mockCategory = new CategoryDTO("NuevaCategoria");
        return mockCategory;
        }catch (HttpClientErrorException e){
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
            user.deleteCurrentAccount();
            throw e;
        }
    }

    public void deleteCategory(CategoryDTO categoryDTO){
        try {
        CategoryDTO mockCategory = new CategoryDTO("NuevaCategoria");
        return;
        }catch (HttpClientErrorException e){
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
            user.deleteCurrentAccount();
        throw e;
        }
    }

}
