package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.TaxRate;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
    Optional<TaxRate> findByFiscalYearIdAndClassification(Long fiscalYearId, PropertyClassification classification);
    List<TaxRate> findByFiscalYearId(Long fiscalYearId);
}
