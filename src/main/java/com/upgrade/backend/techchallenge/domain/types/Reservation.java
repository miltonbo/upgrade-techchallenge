package com.upgrade.backend.techchallenge.domain.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    private String externalId;
    private String arrivalDate;
    private String departureDate;
    private Byte status;
    private Long personId;

}
