package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumber(String identificationNumber);
}
