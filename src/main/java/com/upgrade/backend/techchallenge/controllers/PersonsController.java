package com.upgrade.backend.techchallenge.controllers;

import com.upgrade.backend.techchallenge.domain.exceptions.ApiException;
import com.upgrade.backend.techchallenge.domain.types.Person;
import com.upgrade.backend.techchallenge.repositories.PersonRepository;
import com.upgrade.backend.techchallenge.services.PersonService;
import com.upgrade.backend.techchallenge.utils.transformers.PersonTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/persons")
@Slf4j
@RequiredArgsConstructor
public class PersonsController {

    private final PersonService personService;
    private final PersonRepository personRepository;

    @GetMapping
    public List<Person> getPersons() {
        return StreamSupport.stream(personRepository.findAll().spliterator(), false)
                .map(PersonTransformer::toType)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        try {
            return new ResponseEntity(personService.create(person), HttpStatus.CREATED);
        } catch (ApiException e) {
            return new ResponseEntity(e.getError(), e.getStatus());
        }
    }

}
