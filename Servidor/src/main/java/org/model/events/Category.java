package org.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Category {
    /**
     * Representa una categoría de evento.
     * @param title Título de la categoría
     */
    @Id
    String title;
}
