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
import java.util.Objects;
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
                return " - " + ( title != null ? title : "Sin titulo") + "\n" + ( description != null ? (description.length()<1000 ? description : description.substring(0, 996).concat("...")): "Sin descripcion");
        }
        public String asDetailedString(){
                return "Titulo: " +
                        ( title != null ? title : "Sin titulo")+
                        "\nDescripcion: " +
                        ( description != null ? (description.length()<1000 ? description : description.substring(0, 996).concat("...")): "Sin descripcion")+
                        "\nUbicacion: " +
                        ( location != null ? location : "Sin ubicacion")+
                        "\nPrecio: $" +
                        ( price != null ? price : "Sin precio")+
                        "\nCategoria: " +
                        ( category != null && category.getTitle() != null ? category.getTitle() : "Sin categoria")+
                        "\nTags: " +
                        ( tags != null ? String.join(", ", tags.stream().filter(Objects::nonNull).map(TagDTO::getNombre).toList()) : "Sin etiquetas")+
                        "\nEstado: " +
                        ( state != null ? state.toString() : "Sin estado")+
                        "\nDuracion: " +
                        ( durationMinutes != null ? Duration.ofMinutes(durationMinutes).toHours() + "h " + Duration.ofMinutes(durationMinutes).toMinutesPart() + "m " : "Sin duracion");
        }

}
