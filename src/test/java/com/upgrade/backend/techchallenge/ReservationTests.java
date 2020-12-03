package com.upgrade.backend.techchallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.backend.techchallenge.domain.enums.Status;
import com.upgrade.backend.techchallenge.domain.types.Person;
import com.upgrade.backend.techchallenge.domain.types.Reservation;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteRequest;
import com.upgrade.backend.techchallenge.domain.ws.BookCampsiteResponse;
import com.upgrade.backend.techchallenge.domain.ws.ModifyCampsiteReservationRequest;
import com.upgrade.backend.techchallenge.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class ReservationTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();
	ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Test
	List<Reservation> getReservations() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations"),
				HttpMethod.GET, entity, String.class);

		// Assertions
		Assertions.assertTrue(StringUtils.hasLength(response.getBody()));
		Assertions.assertEquals(200, response.getStatusCodeValue());
		try {
			Reservation[] reservations = mapper.readValue(response.getBody(), Reservation[].class);
			Arrays.stream(reservations).forEach(reservation -> {
				Assertions.assertTrue(StringUtils.hasLength(reservation.getExternalId()));
				Assertions.assertTrue(StringUtils.hasLength(reservation.getArrivalDate()));
				Assertions.assertTrue(StringUtils.hasLength(reservation.getDepartureDate()));
				Assertions.assertNotNull(reservation.getPersonId());
				Assertions.assertNotNull(reservation.getStatus());
			});
			return Arrays.asList(reservations);
		} catch (JsonProcessingException e) {
			Assertions.fail("Json content couldn't be parsed.");
			return null;
		}
	}

	@Test
	String bookCampsite() throws JsonProcessingException {
		cancellAllReservations();

		String personId = registerPerson();
		String arrivalDate = DateUtil.toString(DateUtil.addDays(new Date(), 2));
		String departureDate = DateUtil.toString(DateUtil.addDays(new Date(), 4));

		BookCampsiteRequest request = new BookCampsiteRequest();
		request.setPersonId(personId);
		request.setArrivalDate(arrivalDate);
		request.setDepartureDate(departureDate);

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(request), headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations"),
				HttpMethod.POST, entity, String.class);

		log.info("Reservation registration response: {}", response.getBody());

		// Assertions
		Assertions.assertTrue(StringUtils.hasLength(response.getBody()));
		Assertions.assertEquals(201, response.getStatusCodeValue());
		try {
			BookCampsiteResponse reservation = mapper.readValue(response.getBody(), BookCampsiteResponse.class);

			Assertions.assertTrue(StringUtils.hasLength(reservation.getReservationId()));
			return reservation.getReservationId();
		} catch (JsonProcessingException e) {
			Assertions.fail("Json content couldn't be parsed.");
			return null;
		}
	}

	@Test
	void bookCampsiteSameOverlappingDates() throws JsonProcessingException {
		bookCampsite(); // Book similar days

		String personId = registerPerson();
		String arrivalDate = DateUtil.toString(DateUtil.addDays(new Date(), 3));
		String departureDate = DateUtil.toString(DateUtil.addDays(new Date(), 5));

		BookCampsiteRequest request = new BookCampsiteRequest();
		request.setPersonId(personId);
		request.setArrivalDate(arrivalDate);
		request.setDepartureDate(departureDate);

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(request), headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations"),
				HttpMethod.POST, entity, String.class);

		log.info("Reservation registration response: {}", response.getBody());

		// Assertions
		Assertions.assertTrue(StringUtils.hasLength(response.getBody()));
		Assertions.assertEquals("Already exists a reservation for those dates.", response.getBody());
		Assertions.assertEquals(409, response.getStatusCodeValue());
	}

	@Test
	void cancelReservation() throws JsonProcessingException {
		String reservationId = bookCampsite();

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations/" + reservationId),
				HttpMethod.DELETE, entity, String.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());

		// Idempotence
		response = restTemplate.exchange(
				createURLWithPort("/reservations/" + reservationId),
				HttpMethod.DELETE, entity, String.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void tryToCancelWrongReservation() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations/WRONG-RESERVATION-ID-65416814365135"),
				HttpMethod.DELETE, entity, String.class);
		Assertions.assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void modifyReservation() throws JsonProcessingException {
		String reservationId = bookCampsite();

		String arrivalDate = DateUtil.toString(DateUtil.addDays(new Date(), 7));
		String departureDate = DateUtil.toString(DateUtil.addDays(new Date(), 8));

		ModifyCampsiteReservationRequest request = new ModifyCampsiteReservationRequest();
		request.setArrivalDate(arrivalDate);
		request.setDepartureDate(departureDate);

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(request), headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/reservations/" + reservationId),
				HttpMethod.PUT, entity, String.class);

		log.info("Reservation modification response: {}", response.getBody());

		// Assertions
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void concurrentReservations() throws InterruptedException, JsonProcessingException {
		cancellAllReservations();

		String personId = registerPerson();
		String arrivalDate = DateUtil.toString(DateUtil.addDays(new Date(), 2));
		String departureDate = DateUtil.toString(DateUtil.addDays(new Date(), 4));
		BookCampsiteRequest request = new BookCampsiteRequest();
		request.setPersonId(personId);
		request.setArrivalDate(arrivalDate);
		request.setDepartureDate(departureDate);
		final String requestBody = mapper.writeValueAsString(request);

		final List<Integer> results = new ArrayList<>();

		ExecutorService pool = Executors.newFixedThreadPool(50);
		for (int i = 0; i < 50; i++) {
			pool.submit(() -> {
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

				ResponseEntity<String> response = restTemplate.exchange(
						createURLWithPort("/reservations"),
						HttpMethod.POST, entity, String.class);

				log.info("Reservation registration status response: {}", response.getStatusCodeValue());

				// Assertions
				if (StringUtils.hasLength(response.getBody())) {
					results.add(response.getStatusCodeValue());
				}
			});
		}

		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.SECONDS);

		Assertions.assertEquals(50, results.size());
		Assertions.assertEquals(1, results.stream().filter(result -> result == 201).count());
		Assertions.assertEquals(49, results.stream().filter(result -> result == 409).count());
	}

	private void cancellAllReservations() {
		List<Reservation> reservations = getReservations();

		if (reservations == null) {
			return;
		}

		reservations.stream().filter(reservation -> reservation.getStatus() == Status.ACTIVE.getValue()).forEach(reservation -> {
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);

			ResponseEntity<String> response = restTemplate.exchange(
					createURLWithPort("/reservations/" + reservation.getExternalId()),
					HttpMethod.DELETE, entity, String.class);
			Assertions.assertEquals(200, response.getStatusCodeValue());
		});

	}

	private String registerPerson() throws JsonProcessingException {
		Long now = System.currentTimeMillis();
		String fullName = String.format("Test %d", now);
		String email = String.format("email%d@myemail.com", now);
		Person person = new Person();
		person.setFullName(fullName);
		person.setEmail(email);

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(person), headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/persons"),
				HttpMethod.POST, entity, String.class);

		log.info("Person registration response: {}", response.getBody());
		// Assertions
		Assertions.assertTrue(StringUtils.hasLength(response.getBody()));
		Assertions.assertEquals(201, response.getStatusCodeValue());
		try {
			Person personResponse = mapper.readValue(response.getBody(), Person.class);
			Assertions.assertTrue(StringUtils.hasLength(personResponse.getEmail()));
			Assertions.assertTrue(StringUtils.hasLength(personResponse.getExternalId()));
			Assertions.assertTrue(StringUtils.hasLength(personResponse.getFullName()));
			Assertions.assertEquals(email, personResponse.getEmail());
			Assertions.assertEquals(fullName, personResponse.getFullName());

			return personResponse.getExternalId();
		} catch (JsonProcessingException e) {
			Assertions.fail("Json content couldn't be parsed.");
			return null;
		}
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
