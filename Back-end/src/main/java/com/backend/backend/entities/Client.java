package com.backend.backend.entities;

import com.backend.backend.Enums.ClientType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id")
    private Engineer engineer;

    /** Colonne legacy (schema existant), synchronisee avec dtype. */
    @Column(name = "client_kind")
    private String clientKind;

    @PrePersist
    void syncClientKind() {
        if (this instanceof Particulier) {
            clientKind = "PARTICULIERS";
        } else if (this instanceof Profesionnel) {
            clientKind = "PROFESIONEL";
        }
    }
}
