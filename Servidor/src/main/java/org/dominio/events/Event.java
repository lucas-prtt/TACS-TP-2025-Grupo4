package org.dominio.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Event {
    String titulo;
    String descripcion;
    LocalDateTime fechaYHoraDeInicio;
    Integer minutosDeDuracion;
    String ubicacion;
    Integer cupoMaximo;
    Integer cupoMinimo;
    BigDecimal precio;
    Category categoria;
    List<Tag> etiquetas;
}
