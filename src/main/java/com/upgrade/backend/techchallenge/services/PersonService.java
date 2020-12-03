package com.upgrade.backend.techchallenge.services;

import com.upgrade.backend.techchallenge.domain.exceptions.ConflictException;
import com.upgrade.backend.techchallenge.domain.entities.PersonEntity;
import com.upgrade.backend.techchallenge.domain.types.Person;
import com.upgrade.backend.techchallenge.repositories.PersonRepository;
import com.upgrade.backend.techchallenge.utils.transformers.PersonTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional
    public Person create(Person person) throws ConflictException {
        List<PersonEntity> personEntityList = personRepository.findByEmail(person.getEmail());
        if (personEntityList.isEmpty()) {
            PersonEntity personEntity = new PersonEntity();
            personEntity.setEmail(person.getEmail());
            personEntity.setFullName(person.getFullName());
            Person newPerson = PersonTransformer.toType(personRepository.save(personEntity));

            log.info("New person created person: {}", newPerson);

            return newPerson;
        } else {
            throw new ConflictException("Email already registered.");
        }
    }

}
