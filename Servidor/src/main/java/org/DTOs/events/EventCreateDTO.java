package org.DTOs.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.model.events.Category;
import org.model.events.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDTO {
  private String title;
  private String description;
  private LocalDateTime startDateTime;
  private Integer durationMinutes;
  private String location;
  private Integer maxParticipants;
  private Integer minParticipants;
  private BigDecimal price;
  private Category category;
  private List<Tag> tags;
  private String image;

  public boolean isValid(){
    return title != null && description != null && startDateTime != null && durationMinutes != null && location != null && maxParticipants != null && price != null;
  }

}