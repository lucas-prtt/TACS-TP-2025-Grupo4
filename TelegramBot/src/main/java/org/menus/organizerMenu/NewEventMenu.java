package org.menus.organizerMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.event.CategoryDTO;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.TagDTO;
import org.exceptions.DateAlreadySetException;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.DateInputHelper;
import org.yaml.snakeyaml.util.Tuple;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NewEventMenu extends MenuState {
    EventDTO eventDTO;
    List<Tuple<Field, String>> fields;
    DateInputHelper dateInputHelper = new DateInputHelper(user);
    public NewEventMenu(TelegramUser user) {
        super(user);
        eventDTO = new EventDTO();
        eventDTO.setUsernameOrganizer(user.getServerAccountUsername());
        try {
            fields = new ArrayList<Tuple<Field, String>>(List.of(
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("title"), user.getLocalizedMessage("title")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("description"), user.getLocalizedMessage("description")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("startDateTime"), user.getLocalizedMessage("startDateTime")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("durationMinutes"), user.getLocalizedMessage("durationMinutes")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("location"), user.getLocalizedMessage("location")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("maxParticipants"), user.getLocalizedMessage("maxParticipants")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("minParticipants"), user.getLocalizedMessage("minParticipants")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("price"), user.getLocalizedMessage("price")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("category"), user.getLocalizedMessage("category")),
                    new Tuple<Field, String>(eventDTO.getClass().getDeclaredField("tags"), user.getLocalizedMessage("tags"))
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
                        return dateInputHelper.respondTo(message);
                    } catch (Exception e){
                        e.printStackTrace();
                        user.setMenu(new MainMenu(user));
                        return user.getLocalizedMessage("errorBackToMainMenu");
                    }


                case "BigDecimal":
                    try {
                        field.set(eventDTO, new BigDecimal(message));
                        fields.removeFirst();
                        break;
                    }catch (Exception e){
                        return user.getLocalizedMessage("wrongBigDecimalFormat");
                    }

                case "Integer":
                    try {
                        field.set(eventDTO, Integer.valueOf(message));
                        fields.removeFirst();
                        break;
                    }catch (Exception e){
                        return user.getLocalizedMessage("wrongIntegerFormat");
                    }

                case "CategoryDTO":
                    field.set(eventDTO, new CategoryDTO(message));
                    fields.removeFirst();
                    break;

                case "List":
                    if (Objects.equals(message, "/stop")) {
                        fields.removeFirst();
                        return user.getLocalizedMessage("endTagsInput") + "\n\n" + user.getLocalizedMessage("eventSuccesfullyCreated");
                    }
                    List<TagDTO> tags = (List<TagDTO>) field.get(eventDTO);
                    if (tags == null)
                        tags = new ArrayList<>();
                    tags.add(new TagDTO(message));
                    field.set(eventDTO, tags);
                    break;

                default:
                    return user.getLocalizedMessage("unsupportedField"); // No debería ocurrir nunca, al menos mientras no se abstraiga este menu
            }
        } catch (IllegalAccessException e) {
            return user.getLocalizedMessage("unknownInputError");
        }
        return null;
    }

    @Override
    public String getQuestion() {
        return " - ";
    }
    @Override
    public SendMessage questionMessage() {
        if(fields.isEmpty()){
            user.getApiClient().postEvent(eventDTO);
            user.setMenu(new MainMenu(user));
            return user.getQuestion();
        }
        try {
            if(fields.getFirst()._1().getType() == LocalDateTime.class)
                return dateInputHelper.questionMessage();
        }catch (DateAlreadySetException e){
            try {
                fields.getFirst()._1().set(eventDTO, dateInputHelper.getDate());
                fields.removeFirst();
            }catch (Exception e2){
                return sendMessageText(user.getLocalizedMessage("internalBotError"));
            }
        }
        return sendMessageText(user.getLocalizedMessage("inputGeneric", user.getLocalizedMessage(fields.getFirst()._2())));
    }
}

