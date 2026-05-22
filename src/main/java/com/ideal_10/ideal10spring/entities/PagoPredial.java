package com.ideal_10.ideal10spring.entities;

import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.enums.MetodoPago;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pagos_prediales")
public class PagoPredial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liquidacion_id", nullable = false)
    private LiquidacionPredial liquidacion;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetodoPago paymentMethod;

    @Size(max = 80)
    @Column(length = 80)
    private String reference;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPago status = EstadoPago.REGISTRADO;
}
