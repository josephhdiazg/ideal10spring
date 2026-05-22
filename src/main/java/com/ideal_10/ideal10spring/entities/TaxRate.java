package com.ideal_10.ideal10spring.entities;

import com.ideal_10.ideal10spring.enums.PropertyClassification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "tax_rates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tax_rates_fiscal_year_classification",
                        columnNames = {"fiscal_year_id", "classification"}
                )
        }
)
public class TaxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fiscal_year_id", nullable = false)
    private FiscalYear fiscalYear;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PropertyClassification classification;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal ratePerThousand;

    @Column(nullable = false)
    private Boolean active = true;
}
