package com.upgrade.backend.techchallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.backend.techchallenge.domain.types.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PersonTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();
	ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@Test
	void getPersons() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/persons"),
				HttpMethod.GET, entity, String.class);

		// Assertions
		Assertions.assertTrue(StringUtils.hasLength(response.getBody()));
		Assertions.assertEquals(200, response.getStatusCodeValue());
		try {
			Person[] persons = mapper.readValue(response.getBody(), Person[].class);
			Arrays.stream(persons).forEach(person -> {
				Assertions.assertTrue(StringUtils.hasLength(person.getEmail()));
				Assertions.assertTrue(StringUtils.hasLength(person.getExternalId()));
				Assertions.assertTrue(StringUtils.hasLength(person.getFullName()));
			});
		} catch (JsonProcessingException e) {
			Assertions.fail("Json content couldn't be parsed.");
		}
	}

	@Test
	void postPersons() throws JsonProcessingException {
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
		} catch (JsonProcessingException e) {
			Assertions.fail("Json content couldn't be parsed.");
		}
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
