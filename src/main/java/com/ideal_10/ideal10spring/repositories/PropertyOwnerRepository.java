package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.PropertyOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyOwnerRepository extends JpaRepository<PropertyOwner, Long> {

    List<PropertyOwner> findByPropertyId(Long propertyId);

    List<PropertyOwner> findByOwnerId(Long ownerId);

    Optional<PropertyOwner> findByPropertyIdAndOwnerId(Long propertyId, Long ownerId);

    boolean existsByPropertyIdAndOwnerId(Long propertyId, Long ownerId);
}
