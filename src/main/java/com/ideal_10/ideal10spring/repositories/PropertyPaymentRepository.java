package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.PropertyPayment;
import com.ideal_10.ideal10spring.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyPaymentRepository extends JpaRepository<PropertyPayment, Long> {

    List<PropertyPayment> findByAssessmentId(Long assessmentId);

    @Query("select coalesce(sum(p.amount), 0) from PropertyPayment p where p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") EstadoPago status);
}
