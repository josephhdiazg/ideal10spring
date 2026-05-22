package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.DetalleLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleLiquidacionRepository extends JpaRepository<DetalleLiquidacion, Long> {

    List<DetalleLiquidacion> findByLiquidacionId(Long liquidacionId);
}
