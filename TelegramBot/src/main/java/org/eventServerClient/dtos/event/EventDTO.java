package org.eventServerClient.dtos.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
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
        public String asShortString(){
                return " - " + title + "\n" + (description.length()<40 ? description : description.substring(0, 36).concat("..."));
        }
        public String asDetailedString(){
                return "Titulo: " + title +
                        "\nDescripcion: " + (description.length()<1000 ? description : description.substring(0, 996).concat("..."))+
                        "\nUbicacion: " + location +
                        "\nPrecio: $" + price +
                        "\nCategoria: " + category.getTitle() +
                        "\nTags: " + String.join(", ", tags.stream().map(TagDTO::getNombre).toList()) +
                        "\nEstado: " + state.toString() +
                        "\nDuracion: " + Duration.ofMinutes(durationMinutes).toHours() + "h " + Duration.ofMinutes(durationMinutes).toMinutesPart() + "m";
        }

}
