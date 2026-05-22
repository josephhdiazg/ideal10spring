package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.DetalleLiquidacionResponse;
import com.ideal_10.ideal10spring.dtos.LiquidacionPredialRequest;
import com.ideal_10.ideal10spring.dtos.LiquidacionPredialResponse;
import com.ideal_10.ideal10spring.entities.DetalleLiquidacion;
import com.ideal_10.ideal10spring.entities.LiquidacionPredial;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.TipoMovimientoLiquidacion;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.DetalleLiquidacionRepository;
import com.ideal_10.ideal10spring.repositories.LiquidacionPredialRepository;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiquidacionPredialService {

    private static final BigDecimal TEMPORARY_TAX_RATE = new BigDecimal("0.01");

    private final LiquidacionPredialRepository liquidacionRepository;
    private final DetalleLiquidacionRepository detalleRepository;
    private final PropertyOwnerRepository propertyOwnerRepository;
    private final PropertyService propertyService;

    @Transactional(readOnly = true)
    public List<LiquidacionPredialResponse> findAll() {
        return liquidacionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LiquidacionPredialResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<LiquidacionPredialResponse> findByProperty(Long propertyId) {
        propertyService.getEntity(propertyId);
        return liquidacionRepository.findByPropertyId(propertyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LiquidacionPredialResponse create(LiquidacionPredialRequest request) {
        Property property = propertyService.getEntity(request.propertyId());
        validatePropertyCanBeLiquidated(property);
        validateUniqueLiquidation(request.propertyId(), request.fiscalYear());

        BigDecimal subtotal = money(property.getCadastralValue().multiply(TEMPORARY_TAX_RATE));
        BigDecimal discount = money(defaultZero(request.discountAmount()));
        BigDecimal interest = money(defaultZero(request.interestAmount()));
        BigDecimal total = subtotal.subtract(discount).add(interest);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Liquidation total cannot be negative");
        }

        LiquidacionPredial liquidacion = new LiquidacionPredial();
        liquidacion.setProperty(property);
        liquidacion.setFiscalYear(request.fiscalYear());
        liquidacion.setIssueDate(LocalDate.now());
        liquidacion.setDueDate(request.dueDate() == null ? LocalDate.now().plusDays(30) : request.dueDate());
        liquidacion.setSubtotal(subtotal);
        liquidacion.setDiscountAmount(discount);
        liquidacion.setInterestAmount(interest);
        liquidacion.setTotalAmount(money(total));
        liquidacion.setBalance(money(total));
        liquidacion.setStatus(EstadoLiquidacion.PENDIENTE);

        LiquidacionPredial saved = liquidacionRepository.save(liquidacion);
        createDetail(saved, TipoMovimientoLiquidacion.CARGO, "Impuesto predial base temporal 1% avaluo", subtotal);
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, TipoMovimientoLiquidacion.DESCUENTO, "Descuento aplicado", discount);
        }
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, TipoMovimientoLiquidacion.INTERES, "Intereses aplicados", interest);
        }
        return toResponse(saved);
    }

    @Transactional
    public LiquidacionPredial updateAfterPayment(LiquidacionPredial liquidacion, BigDecimal paymentAmount) {
        BigDecimal newBalance = money(liquidacion.getBalance().subtract(paymentAmount));
        liquidacion.setBalance(newBalance);
        liquidacion.setStatus(newBalance.compareTo(BigDecimal.ZERO) == 0
                ? EstadoLiquidacion.PAGADA
                : EstadoLiquidacion.PARCIAL);
        return liquidacion;
    }

    public LiquidacionPredial getEntity(Long id) {
        return liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidation not found with id " + id));
    }

    public LiquidacionPredialResponse toResponse(LiquidacionPredial liquidacion) {
        List<DetalleLiquidacionResponse> details = detalleRepository.findByLiquidacionId(liquidacion.getId()).stream()
                .map(this::toDetailResponse)
                .toList();
        return new LiquidacionPredialResponse(
                liquidacion.getId(),
                liquidacion.getProperty().getId(),
                liquidacion.getProperty().getCadastralCode(),
                liquidacion.getFiscalYear(),
                liquidacion.getIssueDate(),
                liquidacion.getDueDate(),
                liquidacion.getSubtotal(),
                liquidacion.getDiscountAmount(),
                liquidacion.getInterestAmount(),
                liquidacion.getTotalAmount(),
                liquidacion.getBalance(),
                liquidacion.getStatus(),
                details
        );
    }

    private void validatePropertyCanBeLiquidated(Property property) {
        if (property.getStatus() != PropertyStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active properties can be liquidated");
        }
        if (propertyOwnerRepository.findByPropertyId(property.getId()).isEmpty()) {
            throw new IllegalArgumentException("Property must have at least one owner before liquidation");
        }
    }

    private void validateUniqueLiquidation(Long propertyId, Integer fiscalYear) {
        liquidacionRepository.findByPropertyIdAndFiscalYear(propertyId, fiscalYear)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Liquidation already exists for this property and fiscal year");
                });
    }

    private void createDetail(
            LiquidacionPredial liquidacion,
            TipoMovimientoLiquidacion movementType,
            String concept,
            BigDecimal amount
    ) {
        DetalleLiquidacion detail = new DetalleLiquidacion();
        detail.setLiquidacion(liquidacion);
        detail.setMovementType(movementType);
        detail.setConcept(concept);
        detail.setAmount(money(amount));
        detalleRepository.save(detail);
    }

    private DetalleLiquidacionResponse toDetailResponse(DetalleLiquidacion detail) {
        return new DetalleLiquidacionResponse(
                detail.getId(),
                detail.getMovementType(),
                detail.getConcept(),
                detail.getAmount()
        );
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
