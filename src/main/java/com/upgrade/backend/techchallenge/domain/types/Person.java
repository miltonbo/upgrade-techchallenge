package com.upgrade.backend.techchallenge.domain.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String externalId;
    private String email;
    private String fullName;

}
