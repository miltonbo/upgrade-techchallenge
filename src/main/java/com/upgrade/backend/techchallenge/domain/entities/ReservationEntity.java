package com.upgrade.backend.techchallenge.domain.entities;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Data
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String externalId = UUID.randomUUID().toString();

    @Column
    @NotNull
    private Integer arrivalDate;

    @Column
    @NotNull
    private Integer departureDate;

    @Column
    @NotNull
    private Byte status;

    @Column
    @NotNull
    private Date modifiedAt;

    @Column
    @NotNull
    private Date cancelledAt;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PersonEntity personEntity;

}
