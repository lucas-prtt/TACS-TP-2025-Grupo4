package org.DTOs.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exceptions.EventWithNullFieldsException;
import org.exceptions.InvalidEventUrlException;
import org.model.events.Category;
import org.model.events.Tag;
import org.utils.Validator;

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
  public void validate(String lang) {
    List<String> nullFields = new ArrayList<>();

    if (title == null) nullFields.add("PROBLEM_NULL_TITLE");
    if (description == null) nullFields.add("PROBLEM_NULL_DESCRIPTION");
    if (startDateTime == null) nullFields.add("PROBLEM_NULL_STARTDATETIME");
    if (durationMinutes == null) nullFields.add("PROBLEM_NULL_DURATIONMINUTES");
    if (location == null) nullFields.add("PROBLEM_NULL_LOCATION");
    if (maxParticipants == null) nullFields.add("PROBLEM_NULL_MAXPARTICIPANTS");
    if (price == null) nullFields.add("PROBLEM_NULL_PRICE");
    if (!nullFields.isEmpty()) {
      throw new EventWithNullFieldsException(
              nullFields
      );
    }
    if(image != null && !Validator.isValidUrlNonLocalhost(image))
      throw new InvalidEventUrlException();
    }
}