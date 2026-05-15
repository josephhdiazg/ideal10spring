package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findByCadastralCodeIgnoreCase(String cadastralCode);

    boolean existsByCadastralCodeIgnoreCase(String cadastralCode);
}
