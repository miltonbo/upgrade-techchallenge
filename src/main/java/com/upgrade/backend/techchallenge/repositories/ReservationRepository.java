package com.upgrade.backend.techchallenge.repositories;

import com.upgrade.backend.techchallenge.domain.entities.ReservationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends CrudRepository<ReservationEntity, Long> {

    @Procedure(value = "REGISTER_RESERVATION")
    Long registerReservation(@Param("arrival_date_in") Integer arrivalDate, @Param("departure_date_in") Integer departureDate,
                                 @Param("person_id_in") Long personId, @Param("active_status_in") Byte activeStatus);

    Optional<ReservationEntity> findByExternalId(String externalId);

    @Procedure(value = "MODIFY_RESERVATION")
    Byte modifyReservation(@Param("arrival_date_in") Integer arrivalDate, @Param("departure_date_in") Integer departureDate,
                            @Param("active_status_in") Byte activeStatus, @Param("reservation_id_in") Long reservationId);

    @Query("SELECT e FROM ReservationEntity e WHERE e.arrivalDate >= :startDate AND e.departureDate <= :endDate AND e.status = :activeStatus")
    List<ReservationEntity> findBetweenIntervalAndActives(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                                          @Param("activeStatus") Byte activeStatus);

}
