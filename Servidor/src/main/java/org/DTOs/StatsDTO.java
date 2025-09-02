package org.DTOs;

public class StatsDTO {
    private long eventsCount;
    private long registrationsCount;
    private long waitlistPromotions;
    private double waitlistConversionRate;

    public StatsDTO(long eventsCount, long registrationsCount, long waitlistPromotions, double waitlistConversionRate) {
        this.eventsCount = eventsCount;
        this.registrationsCount = registrationsCount;
        this.waitlistPromotions = waitlistPromotions;
        this.waitlistConversionRate = waitlistConversionRate;
    }

    public long getEventsCount() { return eventsCount; }
    public long getRegistrationsCount() { return registrationsCount; }
    public long getWaitlistPromotions() { return waitlistPromotions; }
    public double getWaitlistConversionRate() { return waitlistConversionRate; }
}
