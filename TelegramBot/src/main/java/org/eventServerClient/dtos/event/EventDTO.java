package org.eventServerClient.dtos.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.users.TelegramUser;
import org.utils.I18nManager;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Setter
@Getter
@AllArgsConstructor
public class EventDTO {
        UUID id;
        String title;
        String description;
        String usernameOrganizer;
        LocalDateTime startDateTime;
        Integer durationMinutes;
        String location;
        Integer maxParticipants;
        Integer minParticipants;
        BigDecimal price;
        CategoryDTO category;
        List<TagDTO> tags;
        EventStateDTO state;
        Integer registered;
        Integer waitlisted;
        public EventDTO (){
                tags = new ArrayList<>();
        }

        public String asShortString(TelegramUser user){
                return " - " + ( title != null ? title : user.getLocalizedMessage("nullTitle")) + "\n" + ( description != null ? (description.length()<1000 ? description : description.substring(0, 996).concat("...")): user.getLocalizedMessage("nullDescription"));
        }
        public String asDetailedString(TelegramUser user){
                return user.getLocalizedMessage("eventAsDetailedString",
                        (title!=null ? title :  user.getLocalizedMessage("nullTitle")),
                        (description != null ? (description.length() < 1000 ? description : description.substring(0, 996).concat("...")) :  user.getLocalizedMessage("nullDescription")),
                        (location != null ? location : user.getLocalizedMessage("nullLocation")),
                        user.localizeDate(startDateTime),
                        (price != null ? price.toString() :  user.getLocalizedMessage("nullPrice")),
                        (category != null && category.getTitle() != null ? category.getTitle() :  user.getLocalizedMessage("nullCategory")),
                        (tags != null ? String.join(", ", tags.stream().filter(Objects::nonNull).map(TagDTO::getNombre).toList()) :  user.getLocalizedMessage("nullTags")),
                        (state != null ? user.getLocalizedMessage(state.toString()) :  user.getLocalizedMessage("nullState")),
                        (durationMinutes != null ? Duration.ofMinutes(durationMinutes).toHours() + "h " + Duration.ofMinutes(durationMinutes).toMinutesPart() + "m" :  user.getLocalizedMessage("nullDuration")),
                        registered,
                        maxParticipants,
                        waitlisted
                );

        }



}
