package com.upgrade.backend.techchallenge.services;

import com.upgrade.backend.techchallenge.domain.entities.PersonEntity;
import com.upgrade.backend.techchallenge.domain.entities.ReservationEntity;
import com.upgrade.backend.techchallenge.domain.enums.Status;
import com.upgrade.backend.techchallenge.domain.exceptions.BadRequestException;
import com.upgrade.backend.techchallenge.domain.exceptions.ConflictException;
import com.upgrade.backend.techchallenge.domain.exceptions.InternalServerErrorException;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteRequest;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteResponse;
import com.upgrade.backend.techchallenge.domain.ws.CampsiteReservationRequest;
import com.upgrade.backend.techchallenge.domain.ws.ModifyCampsiteReservationRequest;
import com.upgrade.backend.techchallenge.repositories.PersonRepository;
import com.upgrade.backend.techchallenge.repositories.ReservationRepository;
import com.upgrade.backend.techchallenge.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;

    @Transactional
    public BookCampsiteResponse bookCampsite(BookCampsiteRequest request) throws InternalServerErrorException, BadRequestException, ConflictException {
        log.info("Book campsite request: {}", request);
        validate(request);

        Integer arrivalDateInt = DateUtil.toInteger(request.getArrivalDate());
        Integer departureDateInt = DateUtil.toInteger(request.getDepartureDate());
        PersonEntity personEntity = personRepository.findByExternalId(request.getPersonId()).get();

        Long result = reservationRepository.registerReservation(arrivalDateInt, departureDateInt, personEntity.getId(), Status.ACTIVE.getValue());
        if (result != null && result > 0L) {
            ReservationEntity reservationEntity = reservationRepository.findById(result).orElse(null);

            if (reservationEntity != null) {
                return BookCampsiteResponse.builder().reservationId(reservationEntity.getExternalId()).build();
            } else {
                log.error("Shouldn't happen, however a reservation was registered and it couldn't be returned.");
                throw new InternalServerErrorException("Problems registering reservation.");
            }
        } else {
            throw new ConflictException("Already exists a reservation for those dates.");
        }
    }

    public void validate(CampsiteReservationRequest request) throws BadRequestException, InternalServerErrorException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date arrivalDate = sdf.parse(request.getArrivalDate());
            Date departureDate = sdf.parse(request.getDepartureDate());

            long days = TimeUnit.DAYS.convert(departureDate.getTime() - arrivalDate.getTime(), TimeUnit.MILLISECONDS);

            Date now = DateUtil.setMidnight(new Date());
            long daysFromToday = TimeUnit.DAYS.convert(arrivalDate.getTime() - now.getTime(), TimeUnit.MILLISECONDS);

            if (days < 1) {
                throw new BadRequestException("Campsite can be reserved minimum one day.");
            }
            if (days > 3) {
                throw new BadRequestException("Campsite can be reserved maximum three days.");
            }
            if (daysFromToday < 1) {
                throw new BadRequestException("Campsite can be reserved minimum one day ahead.");
            }
            if (daysFromToday > 30) {
                throw new BadRequestException("Campsite can be reserved maximum one month in advance.");
            }

            if (request instanceof BookCampsiteRequest) {
                personRepository.findByExternalId(((BookCampsiteRequest)request).getPersonId())
                        .orElseThrow(() -> new BadRequestException("Person not found."));
            }
        } catch (ParseException e) {
            log.error("Problems getting arrival and departure dates, {} - {}", request.getArrivalDate(), request.getDepartureDate(), e);
            throw new InternalServerErrorException("Problems validating reservation data.");
        }
    }

    public void modifyReservation(ModifyCampsiteReservationRequest request, String reservationId) throws InternalServerErrorException, BadRequestException, ConflictException {
        validate(request);

        Optional<ReservationEntity> reservationEntityOptional = reservationRepository.findByExternalId(reservationId);
        if (reservationEntityOptional.isPresent()) {
            if (reservationEntityOptional.get().getStatus() == Status.CANCELLED.getValue()) {
                throw new BadRequestException("Reservation is cancelled.");
            }
        } else {
            throw new BadRequestException("Reservation not found.");
        }

        Integer arrivalDateInt = DateUtil.toInteger(request.getArrivalDate());
        Integer departureDateInt = DateUtil.toInteger(request.getDepartureDate());
        ReservationEntity reservationEntity = reservationEntityOptional.get();

        byte result = reservationRepository.modifyReservation(arrivalDateInt, departureDateInt, Status.ACTIVE.getValue(), reservationEntity.getId());
        if (result > 0) {
            throw new ConflictException("Already exists a reservation for those dates.");
        }
    }

    public void cancelReservation(String reservationId) throws BadRequestException {
        ReservationEntity reservationEntity = reservationRepository.findByExternalId(reservationId)
                .orElseThrow(() -> new BadRequestException("Reservation not found."));
        if (reservationEntity.getStatus() == Status.ACTIVE.getValue()) {
            reservationEntity.setCancelledAt(new Date());
            reservationEntity.setStatus(Status.CANCELLED.getValue());
            reservationRepository.save(reservationEntity);
        }
    }

    public List<String> getAllAvailableDates(Integer startDateInt, Integer endDateInt) throws BadRequestException {
        if (startDateInt > endDateInt) {
            throw new BadRequestException("Start date can not be greater than end date.");
        }
        if (startDateInt.equals(endDateInt)) {
            throw new BadRequestException("Interval can not be the same day.");
        }

        List<ReservationEntity> reservations = reservationRepository.findBetweenIntervalAndActives(startDateInt, endDateInt, Status.ACTIVE.getValue());

        List<ReservationEntity> reservationEntities = reservations.stream()
                .sorted(Comparator.comparing(ReservationEntity::getArrivalDate))
                .collect(Collectors.toList());

        Integer initInterval = startDateInt;
        List<String> intervals = new ArrayList<>();
        for (ReservationEntity reservationEntity : reservationEntities) {
            if (initInterval < reservationEntity.getArrivalDate()) {
                intervals.add(String.format("%s - %s", DateUtil.toString(initInterval), DateUtil.toString(reservationEntity.getArrivalDate())));
            }
            initInterval = reservationEntity.getDepartureDate();
        }
        if (initInterval < endDateInt) {
            intervals.add(String.format("%s - %s", DateUtil.toString(initInterval), DateUtil.toString(endDateInt)));
        }
        return intervals;
    }
}
