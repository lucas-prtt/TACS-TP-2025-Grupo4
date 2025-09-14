package org.utils;

public class PageNormalizer {
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



    public static Integer normalizeEventsPageNumber(Integer page){
        return normalizePageNumber(page, EVENTS_PAGE_NUMBER_DEFAULT);
    }
    public static Integer normalizeEventsPageLimit(Integer limit){
        return normalizePageLimit(limit, EVENTS_PAGE_LIMIT_DEFAULT, EVENTS_PAGE_LIMIT_MIN, EVENTS_PAGE_LIMIT_MAX);
    }
    public static Integer normalizeRegistrationsPageNumber(Integer page){
        return normalizePageNumber(page, REGISTRATIONS_PAGE_NUMBER_DEFAULT);
    }
    public static Integer normalizeRegistrationsPageLimit(Integer limit){
        return normalizePageLimit(limit, REGISTRATIONS_PAGE_LIMIT_DEFAULT, REGISTRATIONS_PAGE_LIMIT_MIN, REGISTRATIONS_PAGE_LIMIT_MAX);
    }

    public static Integer normalizePageNumber(Integer page, Integer def){
        return page!=null? page : def;
    }
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
