package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PropertyAssessmentRepository extends JpaRepository<PropertyAssessment, Long> {

    Optional<PropertyAssessment> findByPropertyIdAndFiscalYear(Long propertyId, Integer fiscalYear);

    List<PropertyAssessment> findByPropertyId(Long propertyId);

    long countByStatus(EstadoLiquidacion status);

    boolean existsByPropertyIdAndStatusIn(Long propertyId, Collection<EstadoLiquidacion> statuses);

    @Query("select coalesce(sum(l.totalAmount), 0) from PropertyAssessment l")
    BigDecimal sumTotalAmount();

    @Query("select coalesce(sum(l.balance), 0) from PropertyAssessment l where l.status in :statuses")
    BigDecimal sumBalanceByStatuses(@Param("statuses") Collection<EstadoLiquidacion> statuses);
}
