package com.upgrade.backend.techchallenge.utils.transformers;

import com.upgrade.backend.techchallenge.domain.entities.PersonEntity;
import com.upgrade.backend.techchallenge.domain.types.Person;

public class PersonTransformer {

    public static Person toType(PersonEntity personEntity) {
        return Person.builder()
                .email(personEntity.getEmail())
                .externalId(personEntity.getExternalId())
                .fullName(personEntity.getFullName())
                .build();
    }
}
