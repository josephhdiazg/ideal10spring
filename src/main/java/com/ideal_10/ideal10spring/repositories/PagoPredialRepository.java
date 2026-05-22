package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.PagoPredial;
import com.ideal_10.ideal10spring.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PagoPredialRepository extends JpaRepository<PagoPredial, Long> {

    List<PagoPredial> findByLiquidacionId(Long liquidacionId);

    @Query("select coalesce(sum(p.amount), 0) from PagoPredial p where p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") EstadoPago status);
}
