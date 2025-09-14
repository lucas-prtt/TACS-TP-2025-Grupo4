package org.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private long eventsCount;
    private long registrationsCount;
    private long waitlistPromotions;
    private double waitlistConversionRate;

    public long getEventsCount() { return eventsCount; }
    public long getRegistrationsCount() { return registrationsCount; }
    public long getWaitlistPromotions() { return waitlistPromotions; }
    public double getWaitlistConversionRate() { return waitlistConversionRate; }
}
