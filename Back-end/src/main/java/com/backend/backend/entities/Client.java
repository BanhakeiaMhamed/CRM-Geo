package com.backend.backend.entities;

import com.backend.backend.Enums.ClientType;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;




@Entity
@Table(name="clients")
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    private String email;

    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id")
    private Engineer engineer;

}
