package com.ideal_10.ideal10spring.repositories;

import com.ideal_10.ideal10spring.entities.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

    Optional<Municipality> findByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);

    boolean existsByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);

    List<Municipality> findByActiveTrue();
}
