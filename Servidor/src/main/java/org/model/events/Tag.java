package org.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tags")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Tag {
    String nombre;
}
