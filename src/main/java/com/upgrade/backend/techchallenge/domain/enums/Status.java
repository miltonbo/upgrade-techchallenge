package com.upgrade.backend.techchallenge.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {

    ACTIVE((byte) 1),
    CANCELLED((byte) 2);

    private final byte value;

}
