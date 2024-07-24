package com.backend.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ENGINEER")
public class Engineer extends User {

    @OneToMany(mappedBy = "engineer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Client> clients;
}
