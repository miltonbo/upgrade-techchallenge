package com.upgrade.backend.techchallenge.repositories;

import com.upgrade.backend.techchallenge.domain.entities.PersonEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<PersonEntity, Long> {

    List<PersonEntity> findByEmail(String email);

    Optional<PersonEntity> findByExternalId(String externalId);

}
