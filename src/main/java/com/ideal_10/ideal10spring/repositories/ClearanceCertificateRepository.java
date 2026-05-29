package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.ClearanceCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClearanceCertificateRepository extends JpaRepository<ClearanceCertificate, Long> {

    Optional<ClearanceCertificate> findByAssessmentId(Long assessmentId);
}
