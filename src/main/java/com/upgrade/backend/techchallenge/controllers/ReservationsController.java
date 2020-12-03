package com.upgrade.backend.techchallenge.controllers;

import com.upgrade.backend.techchallenge.domain.exceptions.ApiException;
import com.upgrade.backend.techchallenge.domain.exceptions.BadRequestException;
import com.upgrade.backend.techchallenge.domain.exceptions.ConflictException;
import com.upgrade.backend.techchallenge.domain.exceptions.InternalServerErrorException;
import com.upgrade.backend.techchallenge.domain.types.Reservation;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteRequest;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteResponse;
import com.upgrade.backend.techchallenge.domain.ws.ModifyCampsiteReservationRequest;
import com.upgrade.backend.techchallenge.repositories.ReservationRepository;
import com.upgrade.backend.techchallenge.services.ReservationService;
import com.upgrade.backend.techchallenge.utils.DateUtil;
import com.upgrade.backend.techchallenge.utils.transformers.ReservationTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/reservations")
@Slf4j
@RequiredArgsConstructor
public class ReservationsController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return StreamSupport.stream(reservationRepository.findAll().spliterator(), false)
                .map(ReservationTransformer::toType)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<BookCampsiteResponse> bookCampsite(@RequestBody BookCampsiteRequest request) {
        try {
            return new ResponseEntity(reservationService.bookCampsite(request), HttpStatus.CREATED);
        } catch (ApiException e) {
            return new ResponseEntity(e.getError(), e.getStatus());
        }
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity modifyReservation(@RequestBody ModifyCampsiteReservationRequest request, @PathVariable("reservationId") String reservationId) {
        try {
            reservationService.modifyReservation(request, reservationId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity(e.getError(), e.getStatus());
        }
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity cancelReservation(@PathVariable("reservationId") String reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity(e.getError(), e.getStatus());
        }
    }

    @GetMapping("/available-dates")
    public ResponseEntity<List<String>> getAvailableDates(@RequestParam(value = "startDate", required = false) String startDate,
                                                          @RequestParam(value = "endDate", required = false) String endDate) {
        Integer startDateInt;
        if (StringUtils.hasLength(startDate)) {
            startDateInt = DateUtil.toInteger(startDate);
        } else {
            startDateInt = DateUtil.toInteger(new Date());
        }

        Integer endDateInt;
        if (StringUtils.hasLength(endDate)) {
            endDateInt = DateUtil.toInteger(endDate);
        } else {
            endDateInt = DateUtil.toInteger(DateUtil.addDays(DateUtil.toDate(startDateInt), 30));
        }

        try {
            List<String> results = reservationService.getAllAvailableDates(startDateInt, endDateInt);
            return new ResponseEntity(results, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity(e.getError(), e.getStatus());
        }
    }

}
