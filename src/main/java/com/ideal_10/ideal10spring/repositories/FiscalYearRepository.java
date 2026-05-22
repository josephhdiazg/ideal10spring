package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.FiscalYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiscalYearRepository extends JpaRepository<FiscalYear, Long> {
    Optional<FiscalYear> findByYear(Integer year);
    Optional<FiscalYear> findByActiveTrue();
}
