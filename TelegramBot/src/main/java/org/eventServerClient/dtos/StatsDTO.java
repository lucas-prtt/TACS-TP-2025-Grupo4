package org.eventServerClient.dtos;


import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private long eventsCount;
    private long registrationsCount;
    private long waitlistPromotions;
    private double waitlistConversionRate;
}
