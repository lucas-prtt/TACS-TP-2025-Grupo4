package org.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document(collection = "categories")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Category {
    // Getters y setters
    @Id
    private UUID id;
    private String title;

    public String getName() { return title; }
    public void setName(String title) { this.title = title; }
}
