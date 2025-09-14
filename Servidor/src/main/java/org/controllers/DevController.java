//package org.controllers;
//
//import org.DTOs.accounts.AccountCreateDTO;
//import org.DTOs.accounts.AccountResponseDTO;
//import org.DTOs.events.EventDTO;
//import org.DTOs.registrations.RegistrationCreateDTO;
//import org.DTOs.registrations.RegistrationDTO;
//import org.model.enums.RegistrationState;
//import org.model.events.Category;
//import org.model.events.Event;
//import org.model.events.Registration;
//import org.model.accounts.Account;
//import org.model.events.Tag;
//import org.repositories.EventRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.utils.RandomWordGenerator;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.math.RoundingMode;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Stream;
//
//@RestController
//@RequestMapping("/dev")
//public class DevController {
//
//    private final EventRepository eventRepository;
//    private final EventController eventController;
//    private final AccountController accountController;
//    private final RegistrationController registrationController;
//
//    public DevController(EventRepository eventRepository, EventController eventController, AccountController accountController, RegistrationController registrationController) {
//        this.eventRepository = eventRepository;
//        this.eventController = eventController;
//        this.accountController = accountController;
//        this.registrationController = registrationController;
//    }
//    Boolean loadSample = false;
//    Boolean loadBigSample = false;
//
//
//    @PostMapping("/load-sample")
//    public String loadSampleData() {
//        if(loadSample)
//            return "Sample already loaded";
//        loadSample = true;
//        Account organizer = new Account();
//
//        // Crear un evento con los campos obligatorios
//        Event event = new Event(
//                "Tech Meetup",
//                "Evento de prueba para Postman",
//                LocalDateTime.now().plusDays(7), // empieza en 7 días
//                120,                             // duración en minutos
//                "Auditorio Central",
//                2,                               // máximo participantes
//                null,                            // mínimo opcional
//                new BigDecimal("100.00"),        // precio
//                null,                            // categoría opcional
//                null,                             // tags opcionales
//                organizer
//        );
//
//        // Crear algunos usuarios de prueba
//        Account acc1 = new Account( "alice@example.com");
//        Account acc2 = new Account( "bob@example.com");
//        Account acc3 = new Account( "charlie@example.com");
//
//        // Crear inscripciones y registrarlas en el evento
//        Registration reg1 = new Registration( acc1);
//        reg1.setEvent(event);
//        Registration reg2 = new Registration( acc2);
//        reg2.setEvent(event);
//        Registration reg3 = new Registration(acc3);
//        reg3.setEvent(event);
//
//        event.registerParticipant(reg1);
//        event.registerParticipant(reg2);
//        event.registerParticipant(reg3); // este debería ir a la waitlist
//
//        // Guardar el evento con el EventService
//        eventRepository.save(event);
//
//        return "Loaded test event with ID: " + event.getId();
//    }
//
//    @PostMapping("/load-big-sample")
//    public String loadBigSampleData() {
//        if(loadBigSample)
//            return "Sample already loaded";
//        loadBigSample = true;
//
//        int qEvents = 0;
//        int qRegistrations = 0;
//        int qAccounts = 0;
//        int qWaitlist = 0;
//
//        Random random = new Random();
//        List<EventDTO> eventos = new ArrayList<>();
//        String[] USERNAMES = {
//                "juanperez", "maria.lopez", "carlagonzalez", "pedro_ramirez", "analu",
//                "roberto.sanchez", "laura_fernandez", "andresm", "valentina_rios", "lucia.mendez",
//                "diegotorres", "camila_garcia", "sergioh", "paola.cruz", "jose_martinez",
//                "martin.castillo", "sofiarodriguez", "felipe.navarro", "antonela", "ricardolopez",
//                "marianap", "cristian.vega", "ines_rojas", "tomasc", "daniela.ortiz",
//                "gustavo.molina", "carolina", "facundor", "alejandro_v", "emilias",
//                "manuel.ibarra", "julieta", "francisco.b", "rominag", "nicolas_silva",
//                "melina.paz", "luis.flores", "agustina", "sebastian_c", "jimenam"
//        };
//        List<AccountResponseDTO> users = new ArrayList<>();
//        for(String user : USERNAMES){
//            users.add((AccountResponseDTO) accountController.createAccount(new AccountCreateDTO(user)).getBody());
//            qAccounts++;
//        }
//
//        for(int i = random.nextInt(200); i<1000; i++){
//            EventDTO evento = new EventDTO();
//            evento.setTitle("Evento " + i + " " + RandomWordGenerator.randomWord() + " " + RandomWordGenerator.randomWord());
//            evento.setDescription("Descripcion: " + RandomWordGenerator.randomWord() + " "+ RandomWordGenerator.randomWord());
//            evento.setOrganizerId(users.get(random.nextInt(15)).getUuid());
//            evento.setStartDateTime(LocalDateTime.now().plusDays(random.nextInt(100)).plusHours(random.nextInt(24)).plusMinutes(random.nextInt(60)));
//            evento.setDurationMinutes(random.nextInt(200));
//            evento.setLocation("Un lugar, "+RandomWordGenerator.randomWord());
//            evento.setPrice(new BigDecimal(random.nextInt(70000)).divide(new BigDecimal(100), RoundingMode.UP ));
//            evento.setMaxParticipants(10 + random.nextInt(17));
//            evento.setMinParticipants(random.nextInt(1));
//            evento.setCategory(new Category(RandomWordGenerator.randomWord()));
//            evento.setTags(Stream.of(RandomWordGenerator.randomWord(), RandomWordGenerator.randomWord(), RandomWordGenerator.randomWord(), RandomWordGenerator.randomWord()).map(Tag::new).toList());
//            eventos.add((EventDTO) eventController.postEvent(evento).getBody());
//            qEvents++;
//        }
//
//        for (EventDTO evento : eventos){
//            for(int i = 0; i<30; i++){
//                try {
//                    ResponseEntity<RegistrationDTO> rta = (ResponseEntity<RegistrationDTO>) eventController.registerUserToEvent(new RegistrationCreateDTO(evento.getId(), users.get(random.nextInt(30)).getUuid()));
//                    qRegistrations++;
//                    if (Objects.equals(rta.getBody().getState(), RegistrationState.WAITLIST)){
//                        qWaitlist++;
//                    }
//                }catch (Exception ignored){}
//            }
//        }
//        return "Sample loaded: " + qEvents + " events, " + qAccounts + " users, " + qRegistrations + " registrations of which " + qWaitlist + " are in waitlist";
//    }
//
//}
