package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.LiquidacionPredial;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LiquidacionPredialRepository extends JpaRepository<LiquidacionPredial, Long> {

    Optional<LiquidacionPredial> findByPropertyIdAndFiscalYear(Long propertyId, Integer fiscalYear);

    List<LiquidacionPredial> findByPropertyId(Long propertyId);

    long countByStatus(EstadoLiquidacion status);

    boolean existsByPropertyIdAndStatusIn(Long propertyId, Collection<EstadoLiquidacion> statuses);

    @Query("select coalesce(sum(l.totalAmount), 0) from LiquidacionPredial l")
    BigDecimal sumTotalAmount();

    @Query("select coalesce(sum(l.balance), 0) from LiquidacionPredial l where l.status in :statuses")
    BigDecimal sumBalanceByStatuses(@Param("statuses") Collection<EstadoLiquidacion> statuses);
}
