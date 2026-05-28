package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.TaxBenefit;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaxBenefitRepository extends JpaRepository<TaxBenefit, Long> {
    Optional<TaxBenefit> findByCode(String code);

    // Returns active benefits that apply to the given classification or to all (null classification)
    @Query("SELECT b FROM TaxBenefit b WHERE b.active = true AND (b.applicableClassification = :classification OR b.applicableClassification IS NULL)")
    List<TaxBenefit> findActiveByClassification(@Param("classification") PropertyClassification classification);
}
