package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.TagDTO;
import org.menus.MenuState;
import org.users.TelegramUser;
import org.yaml.snakeyaml.util.Tuple;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NewEventMenu extends MenuState {
    EventDTO eventDTO;
    List<Tuple<Field, String>> fields;
    public NewEventMenu(TelegramUser user) {
        super(user);
        eventDTO = new EventDTO();
        eventDTO.setUsernameOrganizer(user.getServerAccountUsername());
        try {
            fields = new ArrayList<Tuple<Field, String>>(List.of(
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("title"), "titulo"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("description"), "descripcion"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("startDateTime"), "fecha de inicio"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("durationMinutes"), "duracion del evento en minutos"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("location"), "ubicacion"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("maxParticipants"), "maxima cantidad de participantes"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("minParticipants"), "minima cantidad de participantes"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("price"), "precio de entrada"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("category"), "categoria"),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("tags"), "etiquetas. ingrese /stop para finalizar")
            ));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String respondTo(String message) {
        Field field = fields.getFirst()._1();
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        try {
            switch (fieldType.getSimpleName()) {
                case "String":
                    field.set(eventDTO, message);
                    fields.removeFirst();
                    break;

                case "LocalDateTime":
                    try {
                        field.set(eventDTO, LocalDateTime.parse(message));
                        fields.removeFirst();
                    } catch (DateTimeParseException e) {
                        return "Formato de fecha y hora inválido.\n Use el formato ISO: yyyy-MM-ddTHH:mm:ss";
                    }
                    break;

                case "BigDecimal":
                    try {
                        field.set(eventDTO, new BigDecimal(message));
                        fields.removeFirst();
                        break;
                    }catch (Exception e){
                        return "Introdujo mal el campo. (Utilice el siguiente formato 123.45)";
                    }

                case "Integer":
                    try {
                        field.set(eventDTO, Integer.valueOf(message));
                        fields.removeFirst();
                        break;
                    }catch (Exception e){
                        return "Introduzca un numero entero\n" + getQuestion();
                    }

                case "CategoryDTO":
                    field.set(eventDTO, new CategoryDTO(message));
                    fields.removeFirst();
                    break;

                case "List":
                    if (Objects.equals(message, "/stop")) {
                        fields.removeFirst();
                        return getQuestion();
                    }
                    List<TagDTO> tags = (List<TagDTO>) field.get(eventDTO);
                    if (tags == null)
                        tags = new ArrayList<>();
                    tags.add(new TagDTO(message));
                    field.set(eventDTO, tags);
                    break;

                default:
                    return "ERROR: Campo no soportado."; // No debería ocurrir nunca, al menos mientras no se abstraiga este menu
            }
        } catch (IllegalAccessException e) {
            return "Error al ingresar el valor, vuelva a intentar.";
        }

        return getQuestion();
    }

    @Override
    public String getQuestion() {
        if(fields.isEmpty()){
            user.getApiClient().postEvent(eventDTO);
            return user.setMainMenuAndRespond();
        }
        return "Ingrese " + fields.getFirst()._2() + ":";
    }
}
