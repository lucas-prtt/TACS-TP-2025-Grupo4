package org.menus.organizerMenu;

import lombok.Getter;
import lombok.Setter;
import org.eventServerClient.dtos.event.EventDTO;
import org.eventServerClient.dtos.event.TagDTO;
import org.menus.MainMenu;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.utils.InlineMenuBuilder;
import org.utils.inputSteps.*;

import java.util.*;
@Getter
@Setter
public class NewEventMenu extends MenuState {
    EventDTO eventDTO;
    List<EventInputStep> inputs = new LinkedList<>();
    Integer index = 0;
    public NewEventMenu() {
        super();
        eventDTO = new EventDTO();
        inputs.add(new StringInputStep("title"));
        inputs.add(new StringInputStep("description"));
        inputs.add(new DateTimeInputStep("startDateTime"));
        inputs.add(new IntegerInputStep("durationMinutes", List.of(15, 30, 60, 120, 180), 0, null));
        inputs.add(new StringInputStep("location"));
        inputs.add(new IntegerInputStep("maxParticipants", List.of(10, 20, 50, 100, 500, 1000), 0, null));
        inputs.add(new IntegerInputStep("minParticipants", List.of(0, 10, 20, 50, 100), -1, null));
        inputs.add(new BigDecimalInputStep("price", "freePrice", 0, null));
        inputs.add(new CategoryDTOInputStep("category"));
        inputs.add(new TagsInputStep("tags"));
        inputs.add(new ConfirmCreationStep());
    }

    @Override
    public String respondTo(String message) {
        if(Objects.equals(message, "/back")){
            if(inputs.get(index) instanceof DateTimeInputStep dateTimeInputStep){
                try {
                    dateTimeInputStep.getHelper().goBackOneStep();
                    return null;
                }catch (Exception ignored){}
                // Si no se puede, vuelve a descripcion
            }
            if(inputs.get(index) instanceof TagsInputStep tagsInputStep){
                if(eventDTO.getTags().isEmpty()){
                    index--;
                }else
                    eventDTO.getTags().removeLast();
                return user.getLocalizedMessage("currentTags", String.join(", ", eventDTO.getTags().stream().map(TagDTO::getNombre).toList()));
            }
            index--;
            if(index >= 0 && inputs.get(index) instanceof DateTimeInputStep dateTimeInputStep){
                try {
                    dateTimeInputStep.getHelper().goBackOneStep();
                    return null;
                }catch (Exception ignored){}
                // No deberia pasar nunca
            }

            if(index < 0 || inputs.size() <= index){
                user.setMenu(new OrganizerMenu());
                return null;
            }
            return null;
        }
        boolean handled = inputs.get(index).handleInput(message, eventDTO, user);
        if(inputs.get(index) instanceof IntegerInputStep step && Objects.equals(step.getFieldName(), "minParticipants") && eventDTO.getMinParticipants() != null && eventDTO.getMinParticipants() > eventDTO.getMaxParticipants()){
            return user.getLocalizedMessage("minParticipantsTooHigh");
        }
        if (handled) {
            index++;
            if (index == inputs.size()) {
                user.getApiClient().postEvent(eventDTO);
                user.setMenu(new MainMenu());
                return user.getLocalizedMessage("eventSuccesfullyCreated");
            }
        }
        if(inputs.get(index) instanceof TagsInputStep tagsInputStep){
            return eventDTO.getTags().isEmpty() ? null : user.getLocalizedMessage("currentTags", String.join(", ", eventDTO.getTags().stream().map(TagDTO::getNombre).toList()));
        }
        return null;
    }

    @Override
    public String getQuestion() {
        return " - ";
    }
    @Override
    public SendMessage questionMessage() {
        SendMessage msg = inputs.get(index).getQuestion(user);
        InlineMenuBuilder.addExtraLocalizedOptions(user,  msg, "/back", "/start");
        return msg;
    }
}

