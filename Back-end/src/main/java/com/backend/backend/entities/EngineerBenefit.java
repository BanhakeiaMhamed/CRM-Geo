package com.backend.backend.entities;

import com.backend.backend.Enums.PeriodType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Agrégat mensuel ou trimestriel du bénéfice généré par un ingénieur.
 */
@Entity
@Table(name = "engineer_benefits",
        uniqueConstraints = @UniqueConstraint(columnNames = {"engineer_id", "period_type", "period_year", "period_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id", nullable = false)
    private Engineer engineer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column(name = "period_year", nullable = false)
    private int year;

    /** Mois (1-12) ou trimestre (1-4) selon periodType. */
    @Column(nullable = false)
    private int periodNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalHours;
}
