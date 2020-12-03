package com.upgrade.backend.techchallenge.domain.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteReservationRequest {

    private String arrivalDate; // Format MM/dd/yyyy
    private String departureDate; // Format MM/dd/yyyy

}
