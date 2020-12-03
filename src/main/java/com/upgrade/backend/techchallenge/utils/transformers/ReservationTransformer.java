package com.upgrade.backend.techchallenge.utils.transformers;

import com.upgrade.backend.techchallenge.domain.entities.ReservationEntity;
import com.upgrade.backend.techchallenge.domain.types.Reservation;
import com.upgrade.backend.techchallenge.utils.DateUtil;

public class ReservationTransformer {

    public static Reservation toType(ReservationEntity entity) {
        return Reservation.builder()
                .externalId(entity.getExternalId())
                .arrivalDate(DateUtil.toString(entity.getArrivalDate()))
                .departureDate(DateUtil.toString(entity.getDepartureDate()))
                .status(entity.getStatus())
                .personId(entity.getPersonEntity().getId())
                .build();
    }
}
