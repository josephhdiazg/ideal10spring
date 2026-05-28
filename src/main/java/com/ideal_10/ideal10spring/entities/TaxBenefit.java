package com.ideal_10.ideal10spring.entities;

import com.ideal_10.ideal10spring.enums.PropertyClassification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "tax_benefits",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tax_benefits_code", columnNames = {"code"})
        }
)
public class TaxBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    // null means applies to all property classifications
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PropertyClassification applicableClassification;

    @Column(nullable = false)
    private Boolean active = true;
}
