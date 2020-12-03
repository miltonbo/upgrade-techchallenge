package com.upgrade.backend.techchallenge.domain.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BookCampsiteRequest extends CampsiteReservationRequest {

    private String personId;

}
