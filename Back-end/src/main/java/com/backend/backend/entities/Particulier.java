package com.backend.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
@DiscriminatorValue("PARTICULIERS")
public class Particulier extends Client {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
