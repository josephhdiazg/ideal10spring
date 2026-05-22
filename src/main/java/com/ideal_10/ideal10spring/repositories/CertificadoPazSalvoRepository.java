package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.CertificadoPazSalvo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificadoPazSalvoRepository extends JpaRepository<CertificadoPazSalvo, Long> {

    Optional<CertificadoPazSalvo> findByLiquidacionId(Long liquidacionId);
}
