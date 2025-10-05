

package org.utils;

/**
 * Utilidad para normalizar parámetros de paginación en endpoints de eventos y registros.
 * Permite establecer valores por defecto, mínimos y máximos para número de página y límite.
 */
public class PageNormalizer {
    /** Configuración global obtenida desde el archivo de propiedades. */
    public static final ConfigManager CONFIG = ConfigManager.getInstance();

    // Valores para events
    private static final int EVENTS_PAGE_NUMBER_DEFAULT = CONFIG.getIntegerOrElse("api.events.page.number.default", 1);
    private static final int EVENTS_PAGE_LIMIT_MIN = CONFIG.getIntegerOrElse("api.events.page.limit.min", 1);
    private static final int EVENTS_PAGE_LIMIT_MAX = CONFIG.getIntegerOrElse("api.events.page.limit.max", 100);
    private static final int EVENTS_PAGE_LIMIT_DEFAULT = CONFIG.getIntegerOrElse("api.events.page.limit.default", 10);
    // Valores para registrations
    private static final int REGISTRATIONS_PAGE_NUMBER_DEFAULT = CONFIG.getIntegerOrElse("api.registrations.page.number.default", 1);
    private static final int REGISTRATIONS_PAGE_LIMIT_MIN = CONFIG.getIntegerOrElse("api.registrations.page.limit.min", 1);
    private static final int REGISTRATIONS_PAGE_LIMIT_MAX = CONFIG.getIntegerOrElse("api.registrations.page.limit.max", 100);
    private static final int REGISTRATIONS_PAGE_LIMIT_DEFAULT = CONFIG.getIntegerOrElse("api.registrations.page.limit.default", 10);

    /**
     * Normaliza el número de página para la paginación de eventos.
     * @param page Número de página recibido (puede ser null).
     * @return Número de página válido (por defecto si es null).
     */
    public static Integer normalizeEventsPageNumber(Integer page){
        return normalizePageNumber(page, EVENTS_PAGE_NUMBER_DEFAULT);
    }

    /**
     * Normaliza el límite de elementos por página para eventos.
     * @param limit Límite recibido (puede ser null).
     * @return Límite válido (por defecto si es null, acotado entre mínimo y máximo).
     */
    public static Integer normalizeEventsPageLimit(Integer limit){
        return normalizePageLimit(limit, EVENTS_PAGE_LIMIT_DEFAULT, EVENTS_PAGE_LIMIT_MIN, EVENTS_PAGE_LIMIT_MAX);
    }

    /**
     * Normaliza el número de página para la paginación de inscripciones.
     * @param page Número de página recibido (puede ser null).
     * @return Número de página válido (por defecto si es null).
     */
    public static Integer normalizeRegistrationsPageNumber(Integer page){
        return normalizePageNumber(page, REGISTRATIONS_PAGE_NUMBER_DEFAULT);
    }

    /**
     * Normaliza el límite de elementos por página para inscripciones.
     * @param limit Límite recibido (puede ser null).
     * @return Límite válido (por defecto si es null, acotado entre mínimo y máximo).
     */
    public static Integer normalizeRegistrationsPageLimit(Integer limit){
        return normalizePageLimit(limit, REGISTRATIONS_PAGE_LIMIT_DEFAULT, REGISTRATIONS_PAGE_LIMIT_MIN, REGISTRATIONS_PAGE_LIMIT_MAX);
    }

    /**
     * Normaliza el número de página genérico.
     * @param page Número de página recibido (puede ser null).
     * @param def Valor por defecto si page es null.
     * @return Número de página válido.
     */
    public static Integer normalizePageNumber(Integer page, Integer def){
        return page!=null? page : def;
    }

    /**
     * Normaliza el límite de elementos por página genérico.
     * @param limit Límite recibido (puede ser null).
     * @param def Valor por defecto si limit es null.
     * @param min Valor mínimo permitido.
     * @param max Valor máximo permitido.
     * @return Límite válido, acotado entre min y max.
     */
    public static Integer normalizePageLimit(Integer limit, Integer def, Integer min, Integer max){
        return limit!=null?
                Math.min(
                        Math.max(
                                limit,
                                min),
                        max
                ) : def ;
    }
}
