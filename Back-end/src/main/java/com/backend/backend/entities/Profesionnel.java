package com.backend.backend.entities;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="clients")
@DiscriminatorValue("PROFESIONEL")
public class Profesionnel extends Client {
    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String siren;

    @Column(nullable = false)
    private String siret;

    @Column(nullable = false)
    private String companyType;
}
