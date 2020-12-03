package com.upgrade.backend.techchallenge.domain.entities;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "person")
@Data
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String externalId = UUID.randomUUID().toString();

    @Column
    @NotNull
    private String email;

    @Column
    @NotNull
    private String fullName;

}
