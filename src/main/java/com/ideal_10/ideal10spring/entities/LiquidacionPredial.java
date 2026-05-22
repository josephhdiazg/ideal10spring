package com.ideal_10.ideal10spring.entities;

import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "liquidaciones_prediales",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_liquidaciones_property_year",
                        columnNames = {"property_id", "fiscal_year"}
                )
        }
)
public class LiquidacionPredial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotNull
    @Column(nullable = false)
    private Integer fiscalYear;

    @NotNull
    @Column(nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal interestAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoLiquidacion status = EstadoLiquidacion.PENDIENTE;
}
